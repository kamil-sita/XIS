package toolset.io;

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
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bufferedImage == null) return Optional.empty();
        return Optional.of(bufferedImage);
    }

    public static File saveImage(BufferedImage image, File file) {
        if (file == null) {
            file = new File("tmplst2.png");
        }
        try {
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}