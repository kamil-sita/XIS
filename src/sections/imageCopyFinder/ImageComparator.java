package sections.imageCopyFinder;

import com.sun.org.apache.bcel.internal.generic.ReturnInstruction;
import universal.tools.BufferedImageTools.BufferedImageIO;
import universal.tools.BufferedImageTools.RGB;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ImageComparator {

    //Minimum similarity for given pair to even be considered similar
    private static final double MINIMUM_SIMILARITY = 0.90;
    //Maximum proportions difference for pair to be considered similar
    private static final double MAXIMUM_PROPORTIONS_DIFFERENCE = 1.1;
    //Size of generated miniature of image
    private static final int GENERATED_MINIATURE_SIZE = 5;

    private ArrayList<ComparableImage> images;
    private ArrayList<ComparableImagePair> imagePairs;

    private boolean initialized = false;

    /**
     * Runs image comparator
     * @param folder
     * @return true if initialized
     */

    public boolean run (File folder) {
        if (!initialize(folder)) return false;
        findPairs();

        return true;
    }

    /**
     * Initializes ImageComparator - finds all images in given folder.
     * @param folder folder containing files to compare
     * @return true if initialized
     */

    private boolean initialize (File folder) {
        File[] files = folder.listFiles();
        if (files == null) return false;
        if (files.length == 0) return false;

        for (File file : files) {
            BufferedImage image = BufferedImageIO.getImage(file);
            if (image != null) {
                ComparableImage comparableImage = new ComparableImage(file, image);
                comparableImage.generateData(GENERATED_MINIATURE_SIZE);
                images.add(comparableImage);
            }
        }
        if (images.size() > 0) {
            initialized = true;
            return true;
        }
        else return false;
    }

    /**
     * finds pairs of similar images
     */

    private void findPairs() {

        for (ComparableImage image1 : images) {
            for (ComparableImage image2 : images) {

                if (image1 == image2) continue;
                double similarity = compareImages(image1, image2);
                if (similarity < MINIMUM_SIMILARITY) {
                    continue;
                }

                imagePairs.add(new ComparableImagePair(image1, image2, similarity));
            }
        }

    }

    /**
     * Calculates equality % of two images, based on RGB>compareToRGB() method
     * @param image1
     * @param image2
     * @return
     */

    public static double compareImages(ComparableImage image1, ComparableImage image2) {
        double image1Proportion = image1.getHeight()/image1.getWidth() * 1.0;
        double image2Proportion = image2.getHeight()/image2.getWidth() * 1.0;

        double imagesProportion = image1Proportion/image2Proportion;
        final double LOWER_PROPORTION_DIFFERENCE = 1/MAXIMUM_PROPORTIONS_DIFFERENCE;
        if (!(LOWER_PROPORTION_DIFFERENCE <= imagesProportion && imagesProportion <= MAXIMUM_PROPORTIONS_DIFFERENCE)) {
            return 0;
        }

        double equality = 0;

        for (int x = 0; x < GENERATED_MINIATURE_SIZE; x++) {
            for (int y = 0; y < GENERATED_MINIATURE_SIZE; y++) {

                RGB rgb1 = new RGB(image1.getSmallImage().getRGB(x, y));
                RGB rgb2 = new RGB(image2.getSmallImage().getRGB(x, y));

                equality += rgb1.compareToRGB(rgb2);

            }
        }

        return equality / (GENERATED_MINIATURE_SIZE * GENERATED_MINIATURE_SIZE);


    }

    public boolean isInitialized() {
        return initialized;
    }
}
