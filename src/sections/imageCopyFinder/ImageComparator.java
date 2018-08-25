package sections.imageCopyFinder;

import sections.ProgressReporter;
import universal.tools.imagetools.bufferedimagetools.BufferedImageIO;
import universal.tools.imagetools.bufferedimagetools.RGB;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public final class ImageComparator extends ProgressReporter {
    //Minimum similarity of hues for given pair to be considered similar:
    private static final double MAXIMUM_HUE_DIFFERENCE = 0.1;

    //Minimum similarity for given pair to even be considered similar
    private static final double MINIMUM_SIMILARITY = 0.90;
    //Maximum proportions difference for pair to be considered similar
    private static final double MAXIMUM_PROPORTIONS_DIFFERENCE = 1.1;
    //Size of generated miniature of image
    private int generatedMiniatureSize;

    //how much of progress is being done in first phase
    private static final double FIRST_PHASE_WEIGHT = 0.7;

    private ArrayList<ComparableImage> images;
    private ArrayList<ComparableImagePair> imagePairs;

    private ImageComparatorStatus status;

    private boolean initialized = false;

    public ImageComparator(int generatedMiniatureSize) {
        this.generatedMiniatureSize = generatedMiniatureSize;
    }

    public ArrayList<ComparableImagePair> getImagePairs() {
        return imagePairs;
    }

    /**
     * Runs image comparator
     * @param folder
     * @return true if initialized
     */

    public boolean initialize(File folder, boolean geometricalMode) {
        if (!loadFiles(folder)) {
            reportProgress("No files found");
            return false;
        }
        findPairs(geometricalMode);
        return true;
    }

    /**
     * Initializes ImageComparator - finds all images in given folder.
     * @param folder folder containing files to compare
     * @return true if initialized
     */

    private boolean loadFiles(File folder) {
        reportProgress("Finding files in folder");
        images = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null) return false;
        if (files.length == 0) return false;

        int i = 0;
        long time = System.nanoTime();


        //TODO maybe do it multithreaded? - at least scaling down
        for (File file : files) {
            if (i >= 10) {
                //calculating estimated time left
                double dt = System.nanoTime() - time;
                dt = dt * (files.length - i) / (i);
                dt /= 1000000000;
                reportProgress("Generating preview for file (" + (i+1) + "/" + files.length + "). Estimated time left for generating previews: " + ((int) (dt)) + " seconds.");
            } else {
                reportProgress("Generating preview for file (" + (i+1) + "/" + files.length + ")");
            }



            reportProgress(i/(1.0 * files.length) * FIRST_PHASE_WEIGHT);
            System.out.println(i+ "/" + files.length);
            i++;
            BufferedImage bufferedImage;
            bufferedImage = BufferedImageIO.getImage(file);
            if (bufferedImage != null) {
                try {
                    //optimalizing this part with multithreading seems not to be worth it, based on my tests
                    ComparableImage comparableImage = new ComparableImage(file, bufferedImage);
                    comparableImage.generateData(generatedMiniatureSize);
                    bufferedImage = null;
                    images.add(comparableImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    private void findPairs(boolean geometricalMode) {
        imagePairs = new ArrayList<>();

        long time = System.nanoTime();

        for (int i = 0; i < images.size(); i++) {
            if (i >= 10) {
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

                reportProgress("Comparing images (" + (i+1) + "/" + images.size() + "). Estimated time left for comparing: " + ((int) (dt)) + " seconds.");
            } else {
                reportProgress("Comparing images (" + (i+1) + "/" + images.size() + ")");
            }

            reportProgress(FIRST_PHASE_WEIGHT + (1 - FIRST_PHASE_WEIGHT) * i/(images.size() * 1.0));

            ComparableImage image1 = images.get(i);

            for (int j = i + 1; j < images.size(); j++) {

                ComparableImage image2 = images.get(j);

                if (image1.getHsb().hueDifference(image2.getHsb()) <= MAXIMUM_HUE_DIFFERENCE) {
                    double similarity = compareImages(image1, image2, geometricalMode);

                    System.out.println(similarity);

                    if (similarity >= MINIMUM_SIMILARITY) {
                        imagePairs.add(new ComparableImagePair(image1, image2, similarity));
                    }
                }



            }
        }
        reportProgress("Images compared");

    }

    /**
     * Calculates equality % of two images, based on RGB>compareToRGB() method
     * @param image1
     * @param image2
     * @return % of similarity between images.
     */

    public double compareImages(ComparableImage image1, ComparableImage image2, boolean geometricalMode) {
        final double POWER = 1.25;
        double image1Proportion = (image1.getHeight() * 1.0)/(image1.getWidth() * 1.0);
        double image2Proportion = (image2.getHeight() * 1.0)/(image2.getWidth() * 1.0);

        double imagesProportion = image1Proportion/image2Proportion;
        final double LOWER_PROPORTION_DIFFERENCE = 1/MAXIMUM_PROPORTIONS_DIFFERENCE;
        if (!(LOWER_PROPORTION_DIFFERENCE <= imagesProportion && imagesProportion <= MAXIMUM_PROPORTIONS_DIFFERENCE)) {
            return 0;
        }

        double equality = 0;

        for (int x = 0; x < generatedMiniatureSize; x++) {
            for (int y = 0; y < generatedMiniatureSize; y++) {

                RGB rgb1 = new RGB(image1.getPreview().getRGB(x, y));
                RGB rgb2 = new RGB(image2.getPreview().getRGB(x, y));

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

    public ImageComparatorStatus getStatus() {
        return status;
    }

    public void setStatus(ImageComparatorStatus status) {
        this.status = status;
    }

    public enum ImageComparatorStatus {
        SUCCESFUL, NO_IMAGES_IN_DIRECTORY, NOT_FOLDER, IO_ERROR, NO_PAIRS
    }
}
