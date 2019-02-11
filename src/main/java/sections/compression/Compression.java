package sections.compression;

import javafx.application.Platform;
import pl.ksitarski.simplekmeans.KMeans;
import toolset.imagetools.YCbCrImage;
import toolset.imagetools.YCbCrLayer;
import toolset.io.GuiFileIO;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

public class Compression {

    private static final int ALG_NAME = 0x010571C2;
    private static final short VERSION = 0;
    private static final short LEGACY_VERSION = 0;

    public static final int MIN_K = 2;
    public static final int MAX_K = 32;
    private static final int K_SIZE = 5; // 2^K_SIZE = MAX_K

    private static final double YWEIGHT = 2;
    private static final double CWEIGHT = 1;

    // Recommended Y/Cweight:
    // 128, 64 for high quality images
    // 16, 8 for photos

    private static final int PRE_ITERATIONS = 5;
    private static final int POST_ITERATIONS = 5;

    private static final int BLOCK_SIZE = 128;

    public static void compress() {
        BitSequence b = generateHeader();
        DEBUG_printAsBits(b);
        final BufferedImage[] image = {null};
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            image[0] = GuiFileIO.getImage().get();
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        var ycbcr = new YCbCrImage(image[0]);
        int size_X = image[0].getWidth()/BLOCK_SIZE;
        int size_Y = image[0].getHeight()/BLOCK_SIZE;

        for (int x = 0; x < size_X; x++) {
            for (int y =0; y < size_Y; y++) {
                compressBlock(x, y, BLOCK_SIZE, b, ycbcr.getYl(), YWEIGHT);
                compressBlock(x, y, BLOCK_SIZE, b, ycbcr.getCbl(), CWEIGHT);
                compressBlock(x, y, BLOCK_SIZE, b, ycbcr.getCrl(), CWEIGHT);
            }
        }

        BufferedImage out = ycbcr.getBufferedImage();

        Platform.runLater(() -> {
            GuiFileIO.saveImage(out);
        });

        System.out.println("Size in kb: " + b.getSize()/8.0/1024.0);

        for (int i =0; i < MAX_K + 1; i++) {
            System.out.println("K = " + i + ":" + CompressionStatistic.kStatistic[i]);
        }


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

        var valueList = new ArrayList<IntKMeans>();

        for (int i = x * size; i < (x+1) * size; i++) {
            for (int j = y * size; j < (y+1) * size; j++) {
                valueList.add(new IntKMeans(layer.get(i, j)));
            }
        }

        CompressionStatistic.kStatistic[k]++;


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

    ///calculates k value for k means
    private static int calculateK(int x, int y, int size, YCbCrLayer input, double multiplier) {
        var valueList = new ArrayList<IntKMeans>();

        for (int i = x * size; i < (x+1) * size; i++) {
            for (int j = y * size; j < (y+1) * size; j++) {
                valueList.add(new IntKMeans(input.get(i, j)));
            }
        }

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
