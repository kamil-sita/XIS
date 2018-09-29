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
}
