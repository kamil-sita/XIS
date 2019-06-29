package XIS.toolset.io;

import XIS.sections.Interruptible;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class BufferedImageIO implements AutoCloseable {
    public static Optional<BufferedImage> getImage(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
            System.out.println(bufferedImage);
        } catch (IOException | IllegalArgumentException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bufferedImage == null) return Optional.empty();
        return Optional.of(bufferedImage);
    }

    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public Optional<BufferedImage> getImageWithFailsafe(File file, Interruptible interruptible) {
        final BufferedImage[] bufferedImage = {null};
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            executorService.submit(() -> {
                try {
                    bufferedImage[0] = ImageIO.read(file);
                    countDownLatch.countDown();
                    System.out.println(bufferedImage[0]);
                } catch (IOException e) {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        } catch (InterruptedException e) {
            if (interruptible != null) interruptible.popup("Failed to load: " + file.getName());
        }
        return Optional.ofNullable(bufferedImage[0]);
    }

    public void close() {
        executorService.shutdown();
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
