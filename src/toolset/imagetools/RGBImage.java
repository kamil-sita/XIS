package toolset.imagetools;

import java.awt.image.BufferedImage;

public class RGBImage {

    private RGB[][] image;

    private double minSaturation = 1000;
    private double maxSaturation = -1;

    private double minBrightness = 1000;
    private double maxBrightness = -1;

    public RGBImage(int width, int height) {
        image = new RGB[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image[y][x] = new RGB();
            }
        }
    }

    public void setRgb(int x, int y, RGB rgb) {
        image[y][x] = rgb;
        var hsb = rgb.toHSB();

        if (hsb.S < minSaturation) {
            minSaturation = hsb.S;
        }
        if (hsb.S > maxSaturation) {
            maxSaturation = hsb.S;
        }

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

    public void scaleBrightnessAndSaturation() {
        var multBrightness = 1.0/(maxBrightness - minBrightness);
        var multSaturation = 1.0/(maxSaturation - minSaturation);

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                var hsb = image[y][x].toHSB();
                hsb.B = (hsb.B - minBrightness) * multBrightness;
                hsb.S = (hsb.S - minSaturation) * multSaturation;
                image[y][x] = hsb.toRGB();
            }
        }
    }

    public void resetContrastCounters() {
        minSaturation = 1000;
        maxSaturation = -1;
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
