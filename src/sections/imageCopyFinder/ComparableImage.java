package sections.imageCopyFinder;

import universal.tools.imagetools.BufferedImageScale;
import universal.tools.imagetools.HSB;
import universal.tools.imagetools.RGB;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * ComparableImage, used to compare images.
 */
public final class ComparableImage {
    //original full-sized image
    private BufferedImage fullSizeImage;
    //scaled down image, to speed up calculations
    private BufferedImage smallImage;
    //file containing image
    private File imageFile;

    private HSB hsb;


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

        RGB rgb = new RGB(BufferedImageScale.getScaledDownImage(smallImage, 1).getRGB(0, 0));
        hsb = rgb.toHSB();

        fullSizeImage = null; //so it's easier to know for GC
    }

    public HSB getHsb() {
        return hsb;
    }

    public BufferedImage getPreview() {
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
