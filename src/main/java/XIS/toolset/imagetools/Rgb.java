package XIS.toolset.imagetools;


import java.awt.*;
import java.util.List;

/**
 * Class representing pixel color in RGB model
 */
public class Rgb {

    public int a = 255;
    public int r;
    public int g;
    public int b;

    public Rgb() {

    }

    public Rgb(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Rgb(int r, int g, int b, int a) {
        this(r, g, b);
        this.a = a;
    }

    public Rgb(int rgba) {
        a = (rgba >> 24) & 0xFF;
        r = (rgba >> 16) & 0xFF;
        g = (rgba >> 8) & 0xFF;
        b = rgba & 0xFF;
    }

    public Rgb(Rgb rgb) {
        this.r = rgb.r;
        this.g = rgb.g;
        this.b = rgb.b;
        this.a = rgb.a;
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
    public double compareToRGB(Rgb secondColor) {
        double totalSimilarity = 3.0;

        totalSimilarity -= Math.abs(r - secondColor.r)/255.0;
        totalSimilarity -= Math.abs(g - secondColor.g)/255.0;
        totalSimilarity -= Math.abs(b - secondColor.b)/255.0;

        return totalSimilarity/3.0;
    }

    /**
     * Corrects rgba values so they stay in their range
     * @return this object
     */
    public Rgb normalize() {
        r = Math.max(0, r);
        r = Math.min(255, r);

        g = Math.max(0, g);
        g = Math.min(255, g);

        b = Math.max(0, b);
        b = Math.min(255, b);

        a = Math.max(0, a);
        a = Math.min(255, a);
        return this;
    }

    public double optimizedDistance(Rgb rgb) {
        return Math.abs(squared(r - rgb.r) + squared(g - rgb.g) + squared(b - rgb.b));
    }

    private double squared(double value) {
        return value * value;
    }


    public Hsb toHSB() {
        float[] hsb = new float[3];
        Color.RGBtoHSB(r, g, b, hsb);
        return new Hsb(hsb[0], hsb[1], hsb[2]);
    }

    public Rgb getCopy() {
        return new Rgb(r, g, b, a);
    }

    public void reduceDepth(int bitDepth) {
        int depth = getDepthConst(bitDepth);
        r = r & depth;
        g = g & depth;
        b = b & depth;
    }

    public static double distance(Rgb point1, Rgb point2) {
        return point1.optimizedDistance(point2);
    }

    public static Rgb meanOfList(List<Rgb> list) {
        if (list == null || list.size() == 0) return null;
        Rgb rgb = new Rgb(0, 0 ,0);
        for (var someRgb : list) {
            rgb.r += someRgb.r;
            rgb.g += someRgb.g;
            rgb.b += someRgb.b;
        }
        rgb.r /= list.size();
        rgb.g /= list.size();
        rgb.b /= list.size();
        return rgb;
    }

    private int getDepthConst(int bitDepth) {
        switch (bitDepth) {
            case 8:
                return 0b11111111;
            case 7:
                return 0b11111110;
            case 6:
                return 0b11111100;
            case 5:
                return 0b11111000;
            case 4:
                return 0b11110000;
            case 3:
                return 0b11100000;
            case 2:
                return 0b11000000;
            case 1:
                return 0b10000000;
            case 0:
                return 0b00000000;
            default:
                return 0b00000000;

        }
    }

    public String toString() {
        return "RGB: r: " + r + ", g: " + g + ", b: " + b;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof Rgb) {
            var orgb = (Rgb) o;
            return r == orgb.r && g == orgb.g && b == orgb.b && a == orgb.a;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toInt();
    }
}
