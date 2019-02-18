package sections.compression;

import toolset.IntegerMath;
import toolset.imagetools.YCbCrLayer;

import java.util.ArrayList;
import java.util.List;

public class CompressionLine {
    private final int NOT_FOUND = -1;
    private final int ONE_COLOR = 0;
    private final int TWO_COLORS = 1;

    private List<Integer> line;
    private List<Integer> palette;
    private boolean full = false;
    private BitSequence compressedLine = null;

    private YCbCrLayer layer;

    private int y;
    private int xStart;
    private int xEnd;

    private final int PALETTE_SIZE;
    private final int ENCODE_SIZE;
    private final int LINE_SIZE;

    /**
     * Constructor for compression
     * @param palette
     * @param layer
     * @param xStart
     * @param xEnd
     * @param y
     * @param encodeSize
     */
    public CompressionLine(List<Integer> palette, YCbCrLayer layer, int xStart, int xEnd, int y, int encodeSize) {
        LINE_SIZE = xEnd - xStart;
        line = new ArrayList<>(LINE_SIZE);
        this.PALETTE_SIZE = palette.size();
        this.palette = palette;
        this.ENCODE_SIZE = encodeSize;
        this.layer = layer;

        this.y = y;
        this.xEnd = xEnd;
        this.xStart = xStart;
    }


    /**
     * Constructor for decompression
     * @param dictionary
     * @param layer
     * @param xStart
     * @param xEnd
     * @param y
     * @param encodeSize
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
        if (line.size() == LINE_SIZE) throw  new IllegalArgumentException("Too much values");
        line.add(v);

    }

    public void lockAdding() {
        if (line.size() != LINE_SIZE) throw new IllegalArgumentException("Locked with not enough values");
        full = true;
    }

    public BitSequence compress() {
        if (compressedLine != null) return compressedLine;

        if (!full) throw new IllegalArgumentException("Can't compress before locking");


        compressedLine = new BitSequence();

        if (canBeRleCompressed()) {
            System.out.print("R");
            compressRle();
        } else if (canBeCompressedDifferential()) {
            System.out.print("D");
            compressDifferential();
        } else {
            System.out.print("S");
            compressSimple();
        }
        return compressedLine;
    }

    private void compressRle() {
        compressedLine.putOne(1);
        int colorId1 = findIdInPaletteOfClosest(line.get(0));
        layer.set(xStart, y, palette.get(colorId1));
        int colorId2 = NOT_FOUND;
        int length = 0;
        for (int i = 1; i < line.size(); i++) {
            int value = line.get(i);
            int id = findIdInPaletteOfClosest(value);
            layer.set(xStart + i, y, palette.get(id));
            length++;
            if (id != colorId1) {
                colorId2 = id;
            }
        }

        if (colorId2 == NOT_FOUND) {
            //entire line is in one color
            compressedLine.putOne(ONE_COLOR);
            compressedLine.put(colorId1, ENCODE_SIZE);
            return;
        }
        compressedLine.putOne(TWO_COLORS);
        compressedLine.put(colorId1, ENCODE_SIZE);
        compressedLine.put(colorId2, ENCODE_SIZE);

        int encodeSizeForLength = IntegerMath.log2(LINE_SIZE);
        compressedLine.put(length, encodeSizeForLength);
    }

    private void compressDifferential() {
        compressedLine.putOne(0);
        compressedLine.putOne(1);
        int lastValueId = NOT_FOUND;
        for (int i = 0; i < line.size(); i++) {
            int value = line.get(i);
            int valueId = findIdInPaletteOfClosest(value);
            layer.set(xStart + i, y, palette.get(valueId));
            if (valueId == lastValueId) {
                compressedLine.putOne(1);
                continue;
            }
            if (i != 0) compressedLine.putOne(0);
            compressedLine.put(valueId, ENCODE_SIZE);
            lastValueId = valueId;
        }
    }

    private void compressSimple() {
        compressedLine.putOne(0);
        compressedLine.putOne(0);
        for (int i = 0; i < line.size(); i++) {
            int value = line.get(i);
            int valueId = findIdInPaletteOfClosest(value);
            layer.set(xStart + i, y, palette.get(valueId));
            compressedLine.put(valueId, ENCODE_SIZE);
        }
    }

    private boolean canBeRleCompressed() {
        int lastId = -1;
        int colors = 0;
        for (var val : line) {
            int valId = findIdInPaletteOfClosest(val);
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
            System.out.println("R");
            decompressRle();
        } else if (isDifferentialCompressed()) {
            System.out.println("D");
            decompressDifferential();
        } else {
            System.out.println("S");
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


    private int findIdInPaletteOfClosest(int value) {
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
