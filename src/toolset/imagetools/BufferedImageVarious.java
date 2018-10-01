package toolset.imagetools;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferedImageVarious {
    public static BufferedImage copyImage(BufferedImage input) {
        BufferedImage imageCopy = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
        Graphics2D g = imageCopy.createGraphics();
        g.drawImage(input, 0, 0, null);
        g.dispose();
        return imageCopy;
    }

    public static RGBImage copyImageToRgbImage(BufferedImage input) {
        var rgbimage = new RGBImage(input.getWidth(), input.getHeight());

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                rgbimage.setRgb(x, y, new RGB(input.getRGB(x, y)));
            }
        }

        return rgbimage;
    }
}
