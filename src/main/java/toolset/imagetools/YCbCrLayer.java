package toolset.imagetools;

import java.util.ArrayList;
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


    public List<Integer> replace(int xstart, int xend, int ystart, int yend, List<Integer> palette) {
        var list = new ArrayList<Integer>();
        for (int x = xstart; x < xend; x++) {
            for (int y = ystart; y < yend; y++) {
                if (x < width() && y < height()) {
                    int value = get(x, y);
                    int valueRepl = findClosestInPalette(value, palette);
                    set(x, y, valueRepl);
                    int idOf = palette.indexOf(valueRepl);
                    list.add(idOf);
                }
            }
        }
        return list;
    }

    private static int findClosestInPalette(int value, List<Integer> palette) {
        Integer best = null;
        double bestDistance = Double.MAX_VALUE;
        for (int i = 0; i < palette.size(); i++) {
            double dist = Math.abs(value - palette.get(i));
            if (dist < bestDistance) {
                best = palette.get(i);
                bestDistance = dist;
            }
        }
        return best;
    }
}
