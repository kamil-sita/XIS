package toolset.imagetools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class BufferedImageIO {
    public static BufferedImage getImage(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
}
