package universal.tools.BufferedImageTools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class BufferedImageIO {
    public static BufferedImage getImage(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
}
