package toolset.imagetools;

import java.awt.image.BufferedImage;

public class BufferedImageLayers {
    public static BufferedImage divide(BufferedImage image0, BufferedImage image1) {
        var output = BufferedImageVarious.copyImage(image0);

        for (int x = 0; x < output.getWidth(); x++) {
            for (int y = 0; y < output.getHeight(); y++) {
                var outRgb = new Rgb();

                var im0Rgb = new Rgb(image0.getRGB(x, y));
                var im1Rgb = new Rgb(image1.getRGB(x, y));

                outRgb.r = Math.min(255, (int) (255.0 * im0Rgb.r / im1Rgb.r));
                outRgb.g = Math.min(255, (int) (255.0 * im0Rgb.g / im1Rgb.g));
                outRgb.b = Math.min(255, (int) (255.0 * im0Rgb.b / im1Rgb.b));

                output.setRGB(x, y, outRgb.toInt());
            }
        }

        return output;
    }
}
