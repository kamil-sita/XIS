package sections.imagecopyfinder;

import sections.UserFeedback;
import toolset.imagetools.Rgb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ImageComparator compares images and for images that have more than MINIMUM_SIMILARITY similarity, outputs them into list of ComparableImagePairs
 */
public final class ImageComparator {

    //Minimum similarity of hues for given pair to be considered similar:
    private static final double MAXIMUM_HUE_DIFFERENCE = 0.1;
    //Minimum similarity for given pair to even be considered similar
    private static final double MINIMUM_SIMILARITY = 0.90;
    //Size of generated miniature of image
    private int generatedMiniatureSize;

    private List<ComparableImage> images;
    private List<ComparableImagePair> imagePairs;

    private ImageComparatorStatus status;


    public ImageComparator(int generatedMiniatureSize) {
        this.generatedMiniatureSize = generatedMiniatureSize;
    }

    public List<ComparableImagePair> getImagePairs() {
        return imagePairs;
    }

    /**
     * Runs image comparator
     * @param folders
     * @return true if initialized
     */
    public boolean initialize(File[] folders, boolean isGeometricalMode) {
        var optionalImages = ComparableImageIO.loadFiles(folders, generatedMiniatureSize);
        if (!optionalImages.isEmpty()) {
            images = optionalImages;
            findPairs(isGeometricalMode);
            return true;
        }
        UserFeedback.reportProgress("No images found");
        return false;
    }


    /**
     * finds pairs of similar images
     */

    private void findPairs(boolean geometricalMode) {
        imagePairs = new ArrayList<>();

        long time = System.nanoTime();

        for (int i = 0; i < images.size(); i++) {
            reportFindPairingProgress(time, i);

            ComparableImage image1 = images.get(i);

            for (int j = i + 1; j < images.size(); j++) {

                ComparableImage image2 = images.get(j);

                addPairIfSimilar(geometricalMode, image1, image2);
            }
        }
        UserFeedback.reportProgress("Images compared");
    }

    private void addPairIfSimilar(boolean geometricalMode, ComparableImage image1, ComparableImage image2) {
        if (image1.getHsb().hueDiff(image2.getHsb()) <= MAXIMUM_HUE_DIFFERENCE) {
            double similarity = compareImages(image1, image2, geometricalMode);
            if (similarity >= MINIMUM_SIMILARITY) {
                imagePairs.add(new ComparableImagePair(image1, image2, similarity));
            }
        }
    }

    private void reportFindPairingProgress(long time, int i) {
        if (i >= 10) {
            double dt = getApproximateTimeLeftComparing(time, i);
            UserFeedback.reportProgress("Comparing images (" + (i+1) + "/" + images.size() + "). Estimated time left for comparing: " + ((int) (dt)) + " seconds.");
        } else {
            UserFeedback.reportProgress("Comparing images (" + (i+1) + "/" + images.size() + ")");
        }

        UserFeedback.reportProgress((1.0*i)/images.size());
    }


    private double getApproximateTimeLeftComparing(long time, int i) {
        //calculating estimated time left
        //if you were to plot comparisons for x-th iteration, formula would go somewhat like this: f(x) = size - x
        //calculating integral of it would be too boring, so instead we will calculate
        //two areas of rectangles

        //rectangle 1
        int base1 = i;
        int height1 = (2 * images.size() - i)/2;

        //rectangle 2
        int base2 = images.size() - i;
        int height2 = (images.size() - i)/2;

        int area1 = base1 * height1;
        int area2 = base2 * height2;

        double dt = System.nanoTime() - time;
        dt = dt * area2/area1;
        dt /= 1000000000;
        return dt;
    }

    /**
     * Calculates equality % of two images, based on RGB>compareToRGB() method
     * @param image1
     * @param image2
     * @return % of similarity between images.
     */

    public double compareImages(ComparableImage image1, ComparableImage image2, boolean geometricalMode) {
        final double POWER = 1.25;

        if (!proportionsAcceptable(image1, image2)) return 0;

        double equality = 0;

        for (int x = 0; x < generatedMiniatureSize; x++) {
            for (int y = 0; y < generatedMiniatureSize; y++) {

                Rgb rgb1 = new Rgb(image1.getPreview().getRGB(x, y));
                Rgb rgb2 = new Rgb(image2.getPreview().getRGB(x, y));

                if (geometricalMode) {
                    equality += Math.pow(1 - rgb1.compareToRGB(rgb2), POWER);
                } else {
                    equality += rgb1.compareToRGB(rgb2);
                }

            }
        }

        if (geometricalMode) {
            return 1 - Math.pow(equality/(generatedMiniatureSize * generatedMiniatureSize), 1/POWER);
        } else {
            return equality / (generatedMiniatureSize * generatedMiniatureSize);
        }
    }

    private boolean proportionsAcceptable(ComparableImage image1, ComparableImage image2) {
        double imagesProportionRatio = image1.getProportion()/image2.getProportion();

        final double MAXIMUM_PROPORTIONS_DIFFERENCE = 1.1;
        final double MINIMUM_PROPORTIONS_DIFFERENCE = 1.0/MAXIMUM_PROPORTIONS_DIFFERENCE;
        if (MINIMUM_PROPORTIONS_DIFFERENCE <= imagesProportionRatio && imagesProportionRatio <= MAXIMUM_PROPORTIONS_DIFFERENCE) {
            return true;
        }
        return false;
    }

    public ImageComparatorStatus getStatus() {
        return status;
    }

    public void setStatus(ImageComparatorStatus status) {
        this.status = status;
    }

    public enum ImageComparatorStatus {
        SUCCESSFUL, NO_IMAGES_IN_DIRECTORY, NOT_FOLDER, IO_ERROR, NO_PAIRS;

        @Override
        public String toString() {
            switch (this) {
                case SUCCESSFUL:
                    return "Success";
                case NO_IMAGES_IN_DIRECTORY:
                    return "No images found in this directory";
                case NOT_FOLDER:
                    return "Not a folder";
                case IO_ERROR:
                    return "General I/O error";
                case NO_PAIRS:
                    return "No pairs of images found";
            }
            return "???";
        }
    }
}
