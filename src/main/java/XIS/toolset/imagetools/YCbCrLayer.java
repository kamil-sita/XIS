package XIS.toolset.imagetools;

import XIS.sections.compression.BitSequence;
import XIS.sections.compression.CompressionLine;
import XIS.sections.compression.Statistic;

import java.util.List;

public class YCbCrLayer {
    private int[][] image;

    public YCbCrLayer(int width, int height) {
        image = new int[height][width];
    }

    public int get(int x, int y) {
        if (x >= width() || y >= height()) throw new IllegalArgumentException("Out of array, at: " + x + ", " + y);
        return image[y][x];
    }


    public void set(int x, int y, int value) {
        image[y][x] = value;
    }

    public int width() {
        return image[0].length;
    }

    public int height() {
        return image.length;
    }


    public void replace(int xStart, int xEnd, int yStart, int yEnd, List<Integer> palette, BitSequence compressionSequence, int encodeSize, boolean allowReordering, Statistic statistic) {
        int newXEnd = xEnd > width() ? width() : xEnd;
        int newYEnd = yEnd > height() ? height() : yEnd;
        BitSequence prevSequence = null;
        for (int y = yStart; y < newYEnd; y++) {
            var compressionLine = new CompressionLine(palette, this, xStart, newXEnd, y, encodeSize, statistic, allowReordering);
            for (int x = xStart; x < newXEnd; x++) {
                compressionLine.put(get(x, y));
            }

            compressionLine.lockAdding();

            var compressedSequence = compressionLine.compress();
            if (compressedSequence.equals(prevSequence)) {
                statistic.addSameLine();
                statistic.addApproxSavedSameLines(compressedSequence.getSize());
            } else {
                prevSequence = compressedSequence;
                compressionSequence.addAll(compressedSequence);
            }
        }
    }

    public void copyLower(int xStart, int xEnd, int y) {
        for (int x = xStart; x < xEnd; x++) {
            int v = get(x, y);
            set(x, y + 1, v);
        }
    }
}
