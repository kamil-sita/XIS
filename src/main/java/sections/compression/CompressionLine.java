package sections.compression;

import toolset.IntegerMath;
import toolset.imagetools.YCbCrLayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CompressionLine {
    private final int NOT_FOUND = -1;
    private final int ONE_COLOR = 0;
    private final int TWO_COLORS = 1;

    private Statistic statistic;

    private List<Integer> inputLine;
    private List<Integer> palette;
    private List<Integer> quantizedLine;

    private boolean full = false;
    private BitSequence compressedLine = null;

    private YCbCrLayer layer;

    private int y;
    private int xStart;
    private int xEnd;

    private boolean allowReordering;

    private final int PALETTE_SIZE;
    private final int ENCODE_SIZE;
    private final int LINE_SIZE;

    /**
     * Constructor for compression
     */
    public CompressionLine(List<Integer> palette, YCbCrLayer layer, int xStart, int xEnd, int y, int encodeSize, Statistic statistic, boolean allowReordering) {
        LINE_SIZE = xEnd - xStart;
        inputLine = new ArrayList<>(LINE_SIZE);
        this.PALETTE_SIZE = palette.size();
        this.palette = palette;
        this.ENCODE_SIZE = encodeSize;
        this.layer = layer;

        this.statistic = statistic;

        this.allowReordering = allowReordering;

        this.y = y;
        this.xEnd = xEnd;
        this.xStart = xStart;
    }


    /**
     * Constructor for decompression
     */
    public CompressionLine(BitSequence bitSequence, List<Integer> dictionary, YCbCrLayer layer, int xStart, int xEnd, int y, int encodeSize) {
        LINE_SIZE = xEnd - xStart;
        this.PALETTE_SIZE = dictionary.size();
        this.palette = dictionary;
        this.ENCODE_SIZE = encodeSize;
        this.layer = layer;

        this.y = y;
        this.xEnd = xEnd;
        this.xStart = xStart;
        this.compressedLine = bitSequence;
        full = true;
    }

    public void put(int v) {
        if (full) throw new IllegalArgumentException("Can't add new values after finalization");
        if (inputLine.size() == LINE_SIZE) throw  new IllegalArgumentException("Too much values");
        inputLine.add(v);

    }

    public void lockAdding() {
        if (inputLine.size() != LINE_SIZE) throw new IllegalArgumentException("Locked with not enough values");
        full = true;
    }

    public BitSequence compress() {
        if (compressedLine != null) return compressedLine;

        if (!full) throw new IllegalArgumentException("Can't compress before locking");

        quantize();
        compressedLine = new BitSequence();

        if (canBeRleCompressed()) {
            compressRle();
        } else if (canBeCompressedDifferential()) {
            compressDifferential();
        } else {
            compressSimple();
        }
        return compressedLine;
    }

    private void quantize() {
        quantizedLine = new ArrayList<>();
        for (int i = 0; i < inputLine.size(); i++) {
            int id = findIdOfClosestInPalette(inputLine.get(i));
            quantizedLine.add(id);
        }
    }

    private void compressRle() {
        compressedLine.putOne(1);
        int colorId1 = quantizedLine.get(0);
        layer.set(xStart, y, palette.get(colorId1));
        int colorId2 = NOT_FOUND;
        int length = 0;
        for (int i = 1; i < inputLine.size(); i++) {
            int id = quantizedLine.get(i);
            layer.set(xStart + i, y, palette.get(id));
            length++;
            if (id != colorId1) {
                colorId2 = id;
            }
        }

        if (colorId2 == NOT_FOUND) {
            //entire inputLine is in one color
            compressedLine.putOne(ONE_COLOR);
            compressedLine.put(colorId1, ENCODE_SIZE);
            statistic.addRleLineSize(2 + ENCODE_SIZE);
            return;
        }
        compressedLine.putOne(TWO_COLORS);
        compressedLine.put(colorId1, ENCODE_SIZE);
        compressedLine.put(colorId2, ENCODE_SIZE);

        int encodeSizeForLength = IntegerMath.log2(LINE_SIZE);
        compressedLine.put(length, encodeSizeForLength);
        statistic.addRleLineSize(2 + 2 * ENCODE_SIZE + encodeSizeForLength);
    }

    private void compressDifferential() {
        if (allowReordering) {
            reorder();
        }
        compressedLine.putOne(0);
        compressedLine.putOne(1);
        int size = 2;
        int lastValueId = NOT_FOUND;
        for (int i = 0; i < inputLine.size(); i++) {
            int valueId = quantizedLine.get(i);
            layer.set(xStart + i, y, palette.get(valueId));
            if (valueId == lastValueId) {
                compressedLine.putOne(1);
                size += 1;
                continue;
            }
            if (i != 0) {
                size += 1;
                compressedLine.putOne(0);
            }
            compressedLine.put(valueId, ENCODE_SIZE);
            size += ENCODE_SIZE;
            lastValueId = valueId;
        }
        statistic.addDifferentialLinesSize(size);
    }

    /**
     * This method attempts to optimize differential compression with minor reordering. <br/><br/>
     *
     * Changes sequence: <br/>
     * <b>ABAB</b> and similar <br/>
     * <i>into</i>  <br/>
     * <b>AABB</b><br/> <br/>
     *
     * Changes sequence: <br/>
     * <b>BBAB</b> and similar <br/>
     * <i>into</i> <br/>
     * <b>BBBA </b><br/>
     *
     */
    private void reorder() {
        final int REORDER_SIZE = 4;
        for (int i = 0; i < quantizedLine.size(); i+= REORDER_SIZE) {
            if (i + REORDER_SIZE > quantizedLine.size()) return; //can't optimize sequences of sizes other than 4

            //if for any sequence number of unique colors is different than 2, it cannot be optimized
            var list = new ArrayList<Integer>();
            for (int j = i; j < i + REORDER_SIZE; j++) {
                list.add(quantizedLine.get(j));
            }

            int un = getUniqueColors(list);
            if (un != 2) continue;

            statistic.incrementTimesReordered();

            //getting colors and occurrence count
            int color0id = quantizedLine.get(i);
            int color0Occurrences = 1;
            int color1id = NOT_FOUND;

            for (int j = i + 1; j < i + REORDER_SIZE; j++) {
                int id = quantizedLine.get(j);
                if (id == color0id) {
                    color0Occurrences++;
                } else {
                    color1id = id;
                }
            }

            //if color0Occurrences == color1Occurrences == 2 then sequence is AABB. Otherwise it's AAAB.
            if (color0Occurrences == 2) {
                quantizedLine.set(i    , color0id);
                quantizedLine.set(i + 1, color0id);
                quantizedLine.set(i + 2, color1id);
                quantizedLine.set(i + 3, color1id);
            } else {
                int threeOccurencesColor;
                int oneOccurenceColor;
                if (color0Occurrences == 3) {
                    threeOccurencesColor = color0id;
                    oneOccurenceColor = color1id;
                } else {
                    threeOccurencesColor = color1id;
                    oneOccurenceColor = color0id;
                }
                quantizedLine.set(i,     threeOccurencesColor);
                quantizedLine.set(i + 1, threeOccurencesColor);
                quantizedLine.set(i + 2, threeOccurencesColor);
                quantizedLine.set(i + 3, oneOccurenceColor);

            }
        }

    }

    private int getUniqueColors(List<Integer> list) {
        var set = new HashSet<Integer>(list);
        return set.size();
    }

    private void compressSimple() {
        compressedLine.putOne(0);
        compressedLine.putOne(0);
        for (int i = 0; i < inputLine.size(); i++) {
            int valueId = quantizedLine.get(i);
            layer.set(xStart + i, y, palette.get(valueId));
            compressedLine.put(valueId, ENCODE_SIZE);
        }
        statistic.addNormalLineSize(2 + inputLine.size() * ENCODE_SIZE);
    }

    private boolean canBeRleCompressed() {
        int lastId = -1;
        int colors = 0;
        for (var val : inputLine) {
            int valId = findIdOfClosestInPalette(val);
            if (valId != lastId) {
                colors++;
                lastId = valId;
            }
        }
        return colors <= 2;
    }

    private boolean canBeCompressedDifferential() {
        return PALETTE_SIZE > 2;
    }

    public void decompress() {
        if (isRleCompressed()) {
            decompressRle();
        } else if (isDifferentialCompressed()) {
            decompressDifferential();
        } else {
            decompressSimple();
        }
    }

    private boolean isRleCompressed() {
        return compressedLine.getAt(0);
    }

    private boolean isDifferentialCompressed() {
        return !compressedLine.getAt(0) && compressedLine.getAt(1);
    }

    private void decompressRle() {
        compressedLine.consume(1);
        boolean oneColor = ONE_COLOR == compressedLine.getAndConsume(1);

        int color1id = compressedLine.getAndConsume(ENCODE_SIZE);
        int color2id = oneColor ? NOT_FOUND : compressedLine.getAndConsume(ENCODE_SIZE);
        int color1 = palette.get(color1id);
        if (oneColor) {
            for (int x = xStart; x < xEnd; x++) {
                layer.set(x, y, color1);
            }
        } else {
            int encodeSizeForLength = IntegerMath.log2(LINE_SIZE);
            int length = compressedLine.getAndConsume(encodeSizeForLength);
            int color2 = palette.get(color2id);
            int x;
            for (x = xStart; x < xStart + length; x++) {
                layer.set(x, y, color1);
            }
            for (; x < xEnd; x++) {
                layer.set(x, y, color2);
            }
        }
    }

    private void decompressDifferential() {
        compressedLine.consume(2);
        int colorId = compressedLine.getAndConsume(ENCODE_SIZE);
        layer.set(xStart, y, palette.get(colorId));
        for (int x = xStart + 1; x < xEnd; x++) {
            int nextSame = compressedLine.getAndConsume(1);
            if (nextSame == 1) {
                layer.set(x, y, palette.get(colorId));
            } else {
                colorId = compressedLine.getAndConsume(ENCODE_SIZE);
                layer.set(x, y, palette.get(colorId));
            }
        }
    }

    private void decompressSimple() {
        compressedLine.consume(2);
        for (int x = xStart; x < xEnd; x++) {
            layer.set(x, y, palette.get(compressedLine.getAndConsume(ENCODE_SIZE)));
        }
    }


    private int findIdOfClosestInPalette(int value) {
        int indexOfBest = NOT_FOUND;
        double bestDistance = Double.MAX_VALUE;
        for (int i = 0; i < palette.size(); i++) {
            double dist = Math.abs(value - palette.get(i));
            if (dist < bestDistance) {
                bestDistance = dist;
                indexOfBest = i;
            }
        }
        return indexOfBest;
    }


}
