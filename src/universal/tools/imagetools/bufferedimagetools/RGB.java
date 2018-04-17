package universal.tools.imagetools.bufferedimagetools;


/**
 * Class representing color in RGB model
 */
public class RGB {

    public int a = 255;
    public int r;
    public int g;
    public int b;

    public RGB (int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public RGB (int r, int g, int b, int a) {
        this(r, g, b);
        this.a = a;
    }

    public RGB (int rgba) {
        a = (rgba >> 24) & 0xFF;
        r = (rgba >> 16) & 0xFF;
        g = (rgba >> 8) & 0xFF;
        b = rgba & 0xFF;
    }

    /**
     * @return int value of (ARGB)
     */

    public int toInt() {
        int rgba = a;
        rgba = (rgba << 8) + r;
        rgba = (rgba << 8) + g;
        rgba = (rgba << 8) + b;
        return rgba;
    }

    /**
     * Compares two colors, using r g b channels
     * @return % of similarity (100% - equal)
     */

    public double compareToRGB(RGB secondColor) {
        double totalSimilarity = 3.0;

        totalSimilarity -= Math.abs(r - secondColor.r)/255.0;
        totalSimilarity -= Math.abs(g - secondColor.g)/255.0;
        totalSimilarity -= Math.abs(b - secondColor.b)/255.0;

        return totalSimilarity/3.0;

    }
}
