package sections.compression;

import pl.ksitarski.simplekmeans.KMeans;
import sections.Interruptible;
import sections.MockInterruptible;
import toolset.IntegerMath;
import toolset.imagetools.YCbCrImage;
import toolset.imagetools.YCbCrLayer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public class LosticCompression {

    private static final int ALG_NAME = 0x010571C2;
    private static final short VERSION = 0;
    private static final short LEGACY_VERSION = 0;

    public static final int MIN_K = 2;
    public static final int MAX_K = 32;
    private static final int K_SIZE = 5; // 2^K_SIZE = MAX_K


    private static final int PRE_ITERATIONS = 5;
    private static final int POST_ITERATIONS = 5;


    /**
     * compress() delegate for outside calls to the function
     *
     * @param yWeight
     * @param cWeight
     * @param blockSize
     * @return
     */
    public static Optional<CompressedAndPreview> compressedAndPreview(double yWeight, double cWeight, double blockSize, BufferedImage input) {
        return compress(yWeight, cWeight, blockSize, new MockInterruptible(), input);
    }

    /**
     * @param yWeight
     * @param cWeight
     * @param blockSize
     * @param interruptible
     * @return
     */
    public static Optional<CompressedAndPreview> compress(double yWeight, double cWeight, double blockSize, Interruptible interruptible, BufferedImage input) {
        if (input == null) return Optional.empty();
        BitSequence b = generateBitSequenceWithHeader((int) blockSize, input.getWidth(), input.getHeight());
        var ycbcr = new YCbCrImage(input);
        int size_X = (int) Math.ceil(input.getWidth() / blockSize);
        int size_Y = (int) Math.ceil(input.getHeight() / blockSize);

        KMeans.setLogger(s -> {});

        int i = 0;
        for (int y = 0; y < size_Y; y++) {
            for (int x = 0; x < size_X; x++) {
                System.out.println("Y");
                compressBlock(x, y, (int) blockSize, b, ycbcr.getYl(), yWeight);
                System.out.println("Cb");
                compressBlock(x, y, (int) blockSize, b, ycbcr.getCbl(), cWeight);
                System.out.println("Cr");
                compressBlock(x, y, (int) blockSize, b, ycbcr.getCrl(), cWeight);

                i++;
                interruptible.reportProgress(1.0 * i/ (size_X * size_Y));
            }
        }

        BufferedImage preview = ycbcr.getBufferedImage();


        return Optional.of(new CompressedAndPreview(b, preview));
    }

    public static Optional<BufferedImage> decompress(BitSequence bitSequence) {
        return decompress(bitSequence, new MockInterruptible());
    }

    public static Optional<BufferedImage> decompress(BitSequence bitSequence, Interruptible interruptible) {

        if (bitSequence == null) return Optional.empty();
        bitSequence.resetPointer();
        Header h = new Header(bitSequence);
        if (!headerOkay(h)) {
            return Optional.empty();
        }


        int size_X = (int) Math.ceil(1.0 * h.width / h.blockSize);
        int size_Y = (int) Math.ceil(1.0 * h.height / h.blockSize);

        var image = new YCbCrImage(h.width, h.height);

        int i = 0;
        for (int y = 0; y < size_Y; y++) {
            for (int x = 0; x < size_X; x++) {
                boolean flag;
                flag = decompressBlock(x, y, h.blockSize, bitSequence, image.getYl(), h);
                flag = flag & decompressBlock(x, y, h.blockSize, bitSequence, image.getCbl(), h);
                flag = flag & decompressBlock(x, y, h.blockSize, bitSequence, image.getCrl(), h);
                if (!flag) {
                    return Optional.empty();
                }
                i++;
                interruptible.reportProgress(1.0 * i/ (size_X * size_Y));
            }
        }

        System.out.println(bitSequence.getSize());

        return Optional.of(image.getBufferedImage());
    }

    private static boolean headerOkay(Header h) {
        return h.legacyVersion <= VERSION && !h.errors && h.algName == ALG_NAME;
    }

    private static BitSequence generateBitSequenceWithHeader(int blockSize, int width, int height) {
        BitSequence b = new BitSequence();

        Header h = new Header(
                ALG_NAME,
                VERSION,
                LEGACY_VERSION,
                0,
                width,
                height,
                blockSize
        );
        h.addToBitSequence(b);

        return b;
    }

    private static void compressBlock(int x, int y, int size, BitSequence bitSequence, YCbCrLayer layer, double multiplier) {
        int k = calculateK(x, y, size, layer, multiplier);

        ArrayList<IntKMeans> valueList = getListFromArea(x, y, size, layer);


        KMeans<IntKMeans> kMeansKMeans = new KMeans<>(k, valueList);
        kMeansKMeans.iterate(POST_ITERATIONS);
        var results = kMeansKMeans.getCalculatedMeanPoints();
        var palette = new ArrayList<Integer>();
        for (var result : results) {
            palette.add(result.getVal());
        }

        palette.sort(Integer::compareTo);

        bitSequence.put(k - 1, K_SIZE); //k is min 1, so to use all of the space k-1 is saved instead
        for (int i = 0; i < k; i++) {
            int v = palette.get(i);
            if (v > 255) v = 255;
            bitSequence.put(v, 8);
        }

        final int ENCODE_SIZE = IntegerMath.log2(k);

        layer.replace(x * size, (x + 1) * size, y * size, (y + 1) * size, palette, bitSequence, ENCODE_SIZE);

    }

    private static boolean decompressBlock(int xBlock, int yBlock, int size, BitSequence bitSequence, YCbCrLayer layer, Header header) {
        if (!bitSequence.has(K_SIZE)) return false;
        int dictionarySize = bitSequence.getAndConsume(K_SIZE) + 1;
        int encodeSize = IntegerMath.log2(dictionarySize);
        ArrayList<Integer> dictionary = new ArrayList<>();
        if (!bitSequence.has(8 * dictionarySize)) return false;
        for (int i = 0; i < dictionarySize; i++) {
            dictionary.add(bitSequence.getAndConsume(8));
        }

        int xStart = xBlock * size;
        int xEnd = (xBlock + 1) * size;
        int yStart = yBlock * size;
        int yEnd = (yBlock + 1) * size;

        xEnd = xEnd > header.width ? header.width : xEnd;
        yEnd = yEnd > header.height ? header.height : yEnd;

        System.out.println("Before: " + bitSequence.getConsumedSize());
        try {
            for (int y = yStart; y < yEnd; y++) {

                CompressionLine decompressionLine = new CompressionLine(bitSequence, dictionary, layer,
                        xStart,
                        xEnd,
                        y,
                        encodeSize);
                decompressionLine.decompress();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    private static ArrayList<IntKMeans> getListFromArea(int x, int y, int size, YCbCrLayer layer) {
        var valueList = new ArrayList<IntKMeans>();
        for (int j = y * size; j < (y + 1) * size; j++) {
            for (int i = x * size; i < (x + 1) * size; i++) {
                if (i < layer.width() && j < layer.height()) {
                    valueList.add(new IntKMeans(layer.get(i, j)));
                }
            }
        }
        return valueList;
    }

    ///calculates k value for k means
    private static int calculateK(int x, int y, int size, YCbCrLayer input, double multiplier) {
        var valueList = getListFromArea(x, y, size, input);

        final int INITIAL_RESULTS = 32;

        KMeans<IntKMeans> kMeans = new KMeans<>(INITIAL_RESULTS, valueList);
        kMeans.iterate(PRE_ITERATIONS);
        var results = kMeans.getCalculatedMeanPoints();

        TreeSet<Integer> integerTreeSet = new TreeSet<>();
        for (var result : results) {
            if (result == null) continue;
            int v = result.getVal();
            integerTreeSet.add(v);
        }


        List<Integer> filteredList = new ArrayList<>(integerTreeSet);

        double continuity = calculateContinuity(filteredList);
        double width = calculateWidth(filteredList);

        multiplier = multiplier / 4.0;

        double k = width / continuity * multiplier;

        int k_ceil = (int) Math.ceil(k);
        if (k_ceil > filteredList.size()) k_ceil = filteredList.size();
        k_ceil = Math.max(MIN_K, k_ceil);
        k_ceil = Math.min(MAX_K, k_ceil);
        k_ceil = roundToNextPowerOf2(k_ceil);
        return k_ceil;
    }

    private static int roundToNextPowerOf2(int k) {
        int power = 0;
        while (true) {
            int min = 1 << power;
            int max = 1 << (power + 1);
            if (min < k && k <= max) {
                return max;
            }
            power++;
        }
    }

    private static double calculateContinuity(List<Integer> input) {
        int max = -1;
        int lastElement = 0;
        for (Integer integer : input) {
            if (integer - lastElement > max) {
                max = integer - lastElement;
            }
            lastElement = integer;
        }
        final int MINIMAL_CONTINUITY = 8;
        if (max < MINIMAL_CONTINUITY) return MINIMAL_CONTINUITY;
        return max;
    }

    private static double calculateWidth(List<Integer> input) {
        return input.get(input.size() - 1) - input.get(0);
    }


}
