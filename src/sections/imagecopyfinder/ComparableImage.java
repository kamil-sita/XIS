package sections.imagecopyfinder;

import toolset.imagetools.BufferedImageScale;
import toolset.imagetools.Hsb;
import toolset.imagetools.Rgb;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * ComparableImage is image that can be compared by ImageComparator
 */
public final class ComparableImage {
    //original full-sized image
    private BufferedImage fullSizeImage;
    //scaled down image, to speed up calculations
    private BufferedImage smallImage;
    //file containing image
    private File imageFile;

    private Hsb hsb;


    private int width;
    private int height;


    public ComparableImage(File file, BufferedImage image, final int COMPARED_IMAGE_SIZE) {
        imageFile = file;
        fullSizeImage = image;

        smallImage = BufferedImageScale.getScaledDownImage(fullSizeImage, COMPARED_IMAGE_SIZE);

        width = fullSizeImage.getWidth();
        height = fullSizeImage.getHeight();

        Rgb rgb = new Rgb(BufferedImageScale.getScaledDownImage(smallImage, 1).getRGB(0, 0));
        hsb = rgb.toHSB();

        fullSizeImage = null; //so it's easier to know for GC
    }

    public Hsb getHsb() {
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
