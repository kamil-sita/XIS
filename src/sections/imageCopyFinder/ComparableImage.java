package sections.imageCopyFinder;

import universal.tools.BufferedImageTools.BufferedImageIO;
import universal.tools.BufferedImageTools.BufferedImageScale;

import java.awt.image.BufferedImage;
import java.io.File;

public class ComparableImage {
    private BufferedImage fullSizeImage;
    private BufferedImage smallImage;
    private File imageFile;

    private boolean successfulRead = false;

    private int width;
    private int height;


    public ComparableImage(File file, BufferedImage image) {
        imageFile = file;
        fullSizeImage = image;
    }

    public void generateData(final int COMPARED_IMAGE_SIZE) {
        if (fullSizeImage.equals(null)) return;
        smallImage = BufferedImageScale.getScaledDownImage(fullSizeImage, COMPARED_IMAGE_SIZE);

        width = fullSizeImage.getWidth();
        height = fullSizeImage.getHeight();

        fullSizeImage = null;
        successfulRead = true;
    }

    public BufferedImage getSmallImage() {
        return smallImage;
    }

    public File getImageFile() {
        return imageFile;
    }
}
