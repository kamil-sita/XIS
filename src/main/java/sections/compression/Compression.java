package sections.compression;

import pl.ksitarski.simplekmeans.KMeans;
import sections.Interruptible;
import sections.MockInterruptible;
import toolset.imagetools.YCbCrImage;
import toolset.imagetools.YCbCrLayer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public class Compression {

    private static final int ALG_NAME = 0x010571C2;
    private static final short VERSION = 0;
    private static final short LEGACY_VERSION = 0;

    public static final int MIN_K = 2;
    public static final int MAX_K = 32;
    private static final int K_SIZE = 5; // 2^K_SIZE = MAX_K


    private static final int PRE_ITERATIONS = 5;
    private static final int POST_ITERATIONS = 5;

    private static final int BLOCK_SIZE = 128;

    /**
     * compress() delegate for outside calls to the function
     * @param yWeight
     * @param cWeight
     * @param blockSize
     * @return
     */
    public static Optional<CompressedAndPreview> compressedAndPreview(double yWeight, double cWeight, int blockSize, BufferedImage input) {
        return compress(yWeight, cWeight, blockSize, new MockInterruptible(), input);
    }

    /**
     *
     * @param yWeight
     * @param cWeight
     * @param blockSize
     * @param interruptible
     * @return
     */
    public static Optional<CompressedAndPreview> compress(double yWeight, double cWeight, int blockSize, Interruptible interruptible, BufferedImage input) {
        if (input == null) return Optional.empty();
        BitSequence b = generateHeader();
        var ycbcr = new YCbCrImage(input);
        int size_X = input.getWidth()/BLOCK_SIZE;
        int size_Y = input.getHeight()/BLOCK_SIZE;

        for (int x = 0; x < size_X; x++) {
            for (int y =0; y < size_Y; y++) {
                compressBlock(x, y, BLOCK_SIZE, b, ycbcr.getYl(), yWeight);
                compressBlock(x, y, BLOCK_SIZE, b, ycbcr.getCbl(), cWeight);
                compressBlock(x, y, BLOCK_SIZE, b, ycbcr.getCrl(), cWeight);
            }
        }

        BufferedImage preview = ycbcr.getBufferedImage();

        System.out.println("Size in kb: " + b.getSize()/8.0/1024.0);

        return Optional.of(new CompressedAndPreview(b, preview));
    }

    private static void DEBUG_printAsBits(BitSequence b) {
        var s = b.getSeq();
        int i = 0;
        for (Boolean c : s) {
            if (i%8 == 0 && i != 0) System.out.print(" ");
            i++;
            if (c) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
        }
        System.out.println();
        System.out.println("+++++++++");
    }

    private static BitSequence generateHeader() {
        BitSequence b = new BitSequence();

        b.put(ALG_NAME, 32); //LOSTIC 2
        b.put(VERSION, 8); //current version
        b.put(LEGACY_VERSION, 8); //supported version, that is minimum version that old versions of algorithm may attempt reading
        int flag = 0;
        b.put(flag, 8);

        int width = 1024;
        int height = 1600;

        b.put(width, 32);
        b.put(height, 32);

        b.put(BLOCK_SIZE, 16);
        return b;
    }

    private static void compressBlock(int x, int y, int size, BitSequence bitSequence, YCbCrLayer layer, double multiplier) {
        int k = calculateK(x, y, size, layer, multiplier);

        ArrayList<IntKMeans> valueList = getListFromArea(x, y, size, layer);


        KMeans<IntKMeans> kMeansKMeans = new KMeans<>(k, valueList);
        kMeansKMeans.iterate(POST_ITERATIONS);
        var results = kMeansKMeans.getCalculatedMeanPoints();
        var intResults = new ArrayList<Integer>();
        for (var result : results) {
            intResults.add(result.getVal());
        }

        intResults.sort(Integer::compareTo);

        bitSequence.put(k - 1, K_SIZE); //k is min 1, so to use all of the space k-1 is saved instead
        for (int i = 0; i < k; i++) {
            int v = intResults.get(i);
            if (v > 255) v = 255;
            bitSequence.put(v, 8);
        }

        final int ENCODE_SIZE = intLog2(k);

        var list = layer.replace(x * size, (x + 1) * size, y * size, (y + 1) * size, intResults);

        int lastValue = -1;
        for (int i = 0; i < list.size(); i++) {
            if (ENCODE_SIZE == 1) {
                bitSequence.put(list.get(i), 1);
                continue;
            }
            int value = list.get(i);
            if (value == lastValue) {
                bitSequence.put(1, 1);
            } else {
                bitSequence.put(list.get(i), ENCODE_SIZE + 1);
            }
            lastValue = value;
        }
    }

    private static ArrayList<IntKMeans> getListFromArea(int x, int y, int size, YCbCrLayer layer) {
        var valueList = new ArrayList<IntKMeans>();

        for (int i = x * size; i < (x+1) * size; i++) {
            for (int j = y * size; j < (y+1) * size; j++) {
                valueList.add(new IntKMeans(layer.get(i, j)));
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

        multiplier = multiplier/4.0;

        double k = width/continuity * multiplier;

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

    private static int intLog2(int v) {
        int k = 0;
        while (1 << k != v || 1 << k < 0) {
            k++;
        }
        return k;
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
