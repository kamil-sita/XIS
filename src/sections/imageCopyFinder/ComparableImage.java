package sections.imageCopyFinder;

import universal.tools.BufferedImageTools.BufferedImageIO;
import universal.tools.BufferedImageTools.BufferedImageScale;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * ComparableImage, used to compare images.
 */
public class ComparableImage {
    //original full-sized image
    private BufferedImage fullSizeImage;
    //scaled down image, to speed up calculations
    private BufferedImage smallImage;
    //file containing image
    private File imageFile;


    private int width;
    private int height;


    public ComparableImage(File file, BufferedImage image) {
        imageFile = file;
        fullSizeImage = image;
    }

    public void generateData(final int COMPARED_IMAGE_SIZE) {
        smallImage = BufferedImageScale.getScaledDownImage(fullSizeImage, COMPARED_IMAGE_SIZE);

        width = fullSizeImage.getWidth();
        height = fullSizeImage.getHeight();

        fullSizeImage = null;
    }

    public BufferedImage getSmallImage() {
        return smallImage;
    }

    public File getFile() {
        return imageFile;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
