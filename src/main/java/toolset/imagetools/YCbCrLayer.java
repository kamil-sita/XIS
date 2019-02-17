package toolset.imagetools;

import sections.compression.BitSequence;
import sections.compression.CompressionLine;

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


    public void replace(int xStart, int xEnd, int yStart, int yEnd, List<Integer> palette, BitSequence compressionSequence, int encodeSize) {
        int newXEnd = xEnd > width() ? width() : xEnd;
        int newYEnd = yEnd > height() ? height() : yEnd;
        for (int y = yStart; y < newYEnd; y++) {
            CompressionLine compressionLine = new CompressionLine(palette, this, xStart, newXEnd, y, encodeSize);
            for (int x = xStart; x < newXEnd; x++) {
                compressionLine.put(get(x, y));
            }
            compressionLine.lockAdding();
            compressionSequence.addAll(compressionLine.compress());
        }
    }

}
