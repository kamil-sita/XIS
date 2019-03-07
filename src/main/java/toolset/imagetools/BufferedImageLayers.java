package toolset.imagetools;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferedImageLayers {
    public static BufferedImage divide(BufferedImage image0, BufferedImage image1) {
        var output = sameImage(image0);

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

    public static BufferedImage copyImage(BufferedImage input) {
        if (input == null) return null;
        BufferedImage imageCopy = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
        Graphics2D g = imageCopy.createGraphics();
        g.drawImage(input, 0, 0, null);
        g.dispose();
        return imageCopy;
    }

    private static BufferedImage sameImage(BufferedImage input) {
        return new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }
}
