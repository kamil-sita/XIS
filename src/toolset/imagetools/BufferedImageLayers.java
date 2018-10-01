package toolset.imagetools;

import java.awt.image.BufferedImage;

public class BufferedImageLayers {
    public static RGBImage divide(BufferedImage image0, BufferedImage image1) {
        var output = BufferedImageVarious.copyImageToRgbImage(image0);
        output.resetContrastCounters();

        for (int x = 0; x < output.getWidth(); x++) {
            for (int y = 0; y < output.getHeight(); y++) {
                var outRgb = new RGB();

                var im0Rgb = new RGB(image0.getRGB(x, y));
                var im1Rgb = new RGB(image1.getRGB(x, y));

                outRgb.r = Math.min(255, (int) (255.0 * im0Rgb.r / im1Rgb.r));
                outRgb.g = Math.min(255, (int) (255.0 * im0Rgb.g / im1Rgb.g));
                outRgb.b = Math.min(255, (int) (255.0 * im0Rgb.b / im1Rgb.b));

                output.setRgb(x, y, outRgb);
            }
        }

        return output;
    }
}
