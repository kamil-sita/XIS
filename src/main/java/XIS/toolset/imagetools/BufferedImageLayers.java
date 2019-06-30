package XIS.toolset.imagetools;

import java.awt.*;
import java.awt.image.BufferedImage;

import static XIS.toolset.imagetools.IntArgb.*;

public class BufferedImageLayers {
    public static BufferedImage divide(BufferedImage image0, BufferedImage image1) {
        var output = clone(image0);

        int[] rgb0 = new int[4];
        int[] rgb1 = new int[4];
        int[] out = new int[4];
        out[A] = 255;

        for (int x = 0; x < output.getWidth(); x++) {
            for (int y = 0; y < output.getHeight(); y++) {

                asArray(image0.getRGB(x, y), rgb0);
                asArray(image1.getRGB(x, y), rgb1);

                out[R] = Math.min(255, (int) (255.0 * rgb0[R] / rgb1[R]));
                out[G] = Math.min(255, (int) (255.0 * rgb0[G] / rgb1[G]));
                out[B] = Math.min(255, (int) (255.0 * rgb0[B] / rgb1[B]));

                output.setRGB(x, y, toRgbaInteger(out));
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

    private static BufferedImage clone(BufferedImage input) {
        return new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }
}
