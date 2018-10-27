package toolset.imagetools;

import java.awt.image.BufferedImage;

public class RgbImage {

    private Rgb[][] image;

    private double minBrightness = 1000;
    private double maxBrightness = -1;

    public RgbImage(int width, int height) {
        image = new Rgb[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image[y][x] = new Rgb();
            }
        }
    }

    public void setRgb(int x, int y, Rgb rgb) {
        image[y][x] = rgb;
        var hsb = rgb.toHSB();


        if (hsb.B < minBrightness) {
            minBrightness= hsb.B;
        }
        if (hsb.B > maxBrightness) {
            maxBrightness = hsb.B;
        }

    }

    public BufferedImage getBufferedImage() {
        var out = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                out.setRGB(x, y, image[y][x].toInt());
            }
        }
        return out;
    }

    public void scaleBrightness() {
        var multBrightness = 1.0/(maxBrightness - minBrightness);

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                var hsb = image[y][x].toHSB();
                hsb.B = (hsb.B - minBrightness) * multBrightness;
                image[y][x] = hsb.toRGB();
            }
        }
    }

    public void resetContrastCounters() {
        minBrightness = 1000;
        maxBrightness = -1;
    }

    public int getWidth() {
        return image[0].length;
    }

    public int getHeight() {
        return image.length;
    }


}
