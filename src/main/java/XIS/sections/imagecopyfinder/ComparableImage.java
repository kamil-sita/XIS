package XIS.sections.imagecopyfinder;

import XIS.toolset.imagetools.BufferedImageBlur;
import XIS.toolset.imagetools.BufferedImageScale;
import XIS.toolset.imagetools.Hsb;
import XIS.toolset.imagetools.Rgb;

import java.awt.image.BufferedImage;
import java.io.File;

import static XIS.sections.imagecopyfinder.CompareGroup.*;

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


    public ComparableImage(GroupedFile file, BufferedImage image, final int COMPARED_IMAGE_SIZE) {
        imageFile = file.getFile();
        compareGroup = file.getCompareGroup();
        groupId = file.getGroupId();
        width = image.getWidth();
        height = image.getHeight();

        image = BufferedImageScale.getComparableScaledDownImage(image, COMPARED_IMAGE_SIZE * 2);
        image = BufferedImageBlur.simpleBlur(image, BufferedImageBlur.generateGaussianKernel(3));

        smallImage = BufferedImageScale.getComparableScaledDownImage(image, COMPARED_IMAGE_SIZE);


        Rgb rgb = new Rgb(BufferedImageScale.getComparableScaledDownImage(smallImage, 1).getRGB(0, 0));
        averageHsb = rgb.toHSB();
    }

    public boolean isSameFile(ComparableImage comparableImage) {
        return imageFile.equals(comparableImage.imageFile);
    }

    public boolean canCompare(ComparableImage comparableImage) {

        //localOnly
        if (compareGroup == localOnly) {
            //check if other can compare locally
            if (comparableImage.compareGroup == localOnly || comparableImage.compareGroup == all) { //theoretically the
                // other group cannot have other compareGroup status due to the creation method

                //check if share group
                return groupId == comparableImage.groupId;
            } else {
                return false;
            }
        }

        //globalOnly
        if (compareGroup == globalOnly) {
            if (comparableImage.compareGroup == all || comparableImage.compareGroup == globalOnly) {
                //check if in other groups
                return groupId != comparableImage.groupId;
            }
            return false;
        }

        //all
        if (comparableImage.compareGroup == globalOnly) {
            return groupId != comparableImage.groupId;
        }

        if (comparableImage.compareGroup == localOnly) {
            return groupId == comparableImage.groupId;
        }

        return true;
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
