package toolset.imagetools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public final class BufferedImageIO {
    public static Optional<BufferedImage> getImage(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bufferedImage == null) return Optional.empty();
        return Optional.of(bufferedImage);
    }
}
