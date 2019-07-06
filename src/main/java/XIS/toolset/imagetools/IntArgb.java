package XIS.toolset.imagetools;

import java.awt.*;

/**
 * Class that helps use integer as a pixel color in ARGB model, provided by BufferedImage.
 * Old class (RGB) had too big of a memory footprint.
 */
public final class IntArgb {

    public static final int A = 0;
    public static final int R = 1;
    public static final int G = 2;
    public static final int B = 3;
    private static final int ARR_SIZE = 4;

    public static int[] asArray(int argb) {
        int[] array = new int[ARR_SIZE];
        asArray(argb, array);
        return array;
    }

    public static void asArray(int argb, int[] array) {
        array[A] = (argb >> 24) & 0xFF;
        array[R] = (argb >> 16) & 0xFF;
        array[G] = (argb >> 8) & 0xFF;
        array[B] = argb & 0xFF;
    }

    public static int toRgbaInteger(int r, int g, int b, int a) {
        int rgba = a;
        rgba = (rgba << 8) + r;
        rgba = (rgba << 8) + g;
        rgba = (rgba << 8) + b;
        return rgba;
    }

    public static int toRgbaInteger(int[] argbArray) {
        int rgba = argbArray[A];
        rgba = (rgba << 8) + argbArray[R];
        rgba = (rgba << 8) + argbArray[G];
        rgba = (rgba << 8) + argbArray[B];
        return rgba;
    }

    /**
     * Returns euclidean distance between two colors, between 0 and 255 * sqrt(3). Does not include alpha channel.
     */
    public static double distance(int argb0, int argb1) {
        var argb0v = asArray(argb0);
        var argb1v = asArray(argb1);

        return Math.sqrt(
                square(argb0v[R] - argb1v[R]) +
                square(argb0v[G] - argb1v[G]) +
                square(argb0v[B] - argb1v[B])
        );
    }


    /**
     * Compares two colors, using r g b channels
     * @return % of similarity (100% - equal)
     */
    public static double compare(int[] rgba0, int[] rgba1) {
        double totalSimilarity = 255.0 * 3.0;

        totalSimilarity -= Math.abs(rgba0[R] - rgba1[R]);
        totalSimilarity -= Math.abs(rgba0[G] - rgba1[G]);
        totalSimilarity -= Math.abs(rgba0[B] - rgba1[B]);

        return totalSimilarity/(255.0*3.0);
    }

    private static double square(double value) {
        return value * value;
    }


    public static void ColorspaceRGBtoHSB(int[] argb, float[] hsb) {
        Color.RGBtoHSB(argb[R], argb[G], argb[B], hsb);
    }

    public static int ColorspaceHSBtoIntArgb(float[] hsb) {
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }
}
