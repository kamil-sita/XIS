package sections.imagecopyfinder;

import toolset.imagetools.BufferedImageBlur;
import toolset.imagetools.BufferedImageScale;
import toolset.imagetools.Hsb;
import toolset.imagetools.Rgb;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * ComparableImage is image that can be compared by ImageComparator
 */
public final class ComparableImage {
    //scaled down image, to speed up calculations
    private BufferedImage smallImage;
    //file containing image
    private File imageFile;

    private Hsb averageHsb;

    private CompareGroup compareGroup;
    private int groupId;

    private int width;
    private int height;


    public ComparableImage(File file, BufferedImage image, final int COMPARED_IMAGE_SIZE) {
        imageFile = file;

        width = image.getWidth();
        height = image.getHeight();

        image = BufferedImageScale.getComparableScaledDownImage(image, COMPARED_IMAGE_SIZE * 2);
        image = BufferedImageBlur.simpleBlur(image, BufferedImageBlur.generateGaussianKernel(3));

        smallImage = BufferedImageScale.getComparableScaledDownImage(image, COMPARED_IMAGE_SIZE);


        Rgb rgb = new Rgb(BufferedImageScale.getComparableScaledDownImage(smallImage, 1).getRGB(0, 0));
        averageHsb = rgb.toHSB();
    }

    public double getProportion() {
        return (getHeight() * 1.0)/(getWidth() * 1.0);
    }

    public Hsb getAverageHsb() {
        return averageHsb;
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
