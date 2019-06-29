package XIS.toolset.imagetools;

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
        array[A] = (argb >> 24) & 0xFF;
        array[R] = (argb >> 16) & 0xFF;
        array[G] = (argb >> 8) & 0xFF;
        array[B] = argb & 0xFF;
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

    private static double square(double value) {
        return value * value;
    }

    public static Hsb toHsb(int argb) {
        return new Rgb(argb).toHSB();
    }
}
