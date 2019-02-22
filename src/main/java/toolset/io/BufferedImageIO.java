package toolset.io;

import sections.UserFeedback;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class BufferedImageIO {
    public static Optional<BufferedImage> getImage(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException | IllegalArgumentException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bufferedImage == null) return Optional.empty();
        return Optional.of(bufferedImage);
    }


    public static Optional<BufferedImage> getImageWithFailsafe(File file) {
        final BufferedImage[] bufferedImage = {null};
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            new Thread(() -> {
                try {
                    bufferedImage[0] = ImageIO.read(file);
                    countDownLatch.countDown();
                } catch (IOException e) {
                    countDownLatch.countDown();
                }
            }).start();
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        } catch (InterruptedException e) {
            UserFeedback.popup("Failed to load: " + file.getName());
        }
        if (bufferedImage[0] == null) return Optional.empty();
        return Optional.of(bufferedImage[0]);
    }

    public static File saveImage(BufferedImage image, File file) {
        if (file == null) {
            file = new File("tmpxis.png");
        }
        try {
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
