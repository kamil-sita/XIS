package sections.imageCopyFinder;

import sections.XISProgressReportingClass;
import universal.tools.imagetools.bufferedimagetools.BufferedImageIO;
import universal.tools.imagetools.bufferedimagetools.RGB;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ImageComparator extends XISProgressReportingClass {

    //Minimum similarity for given pair to even be considered similar
    private static final double MINIMUM_SIMILARITY = 0.90;
    //Maximum proportions difference for pair to be considered similar
    private static final double MAXIMUM_PROPORTIONS_DIFFERENCE = 1.1;
    //Size of generated miniature of image
    private static final int GENERATED_MINIATURE_SIZE = 64;

    //how much of progress is being done in first phase
    private static final double FIRST_PHASE_WEIGHT = 0.7;

    private ArrayList<ComparableImage> images;
    private ArrayList<ComparableImagePair> imagePairs;

    private ImageComparatorStatus status;

    private boolean initialized = false;


    public ArrayList<ComparableImagePair> getImagePairs() {
        return imagePairs;
    }

    /**
     * Runs image comparator
     * @param folder
     * @return true if initialized
     */

    public boolean initialize(File folder) {
        if (!loadFiles(folder)) {
            reportProgress("No files found");
            return false;
        }
        findPairs();
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
        for (File file : files) {

            reportProgress("Analyzing file (" + (i+1) + "/" + files.length + ")");
            reportProgress(i/(1.0 * files.length) * FIRST_PHASE_WEIGHT);

            System.out.println(i++ + "/" + files.length);
            BufferedImage bufferedImage;
            try {
                 bufferedImage = BufferedImageIO.getImage(file);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(file.getName());
                continue;
            }
            if (bufferedImage != null) {
                try {
                    ComparableImage comparableImage = new ComparableImage(file, bufferedImage);
                    comparableImage.generateData(GENERATED_MINIATURE_SIZE);
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

    private void findPairs() {
        imagePairs = new ArrayList<>();


        for (int i = 0; i < images.size(); i++) {
            reportProgress("Comparing images (" + (i+1) + "/" + images.size() + ")");
            reportProgress(FIRST_PHASE_WEIGHT + (1 - FIRST_PHASE_WEIGHT) * i/(images.size() * 1.0));

            ComparableImage image1 = images.get(i);

            for (int j = i + 1; j < images.size(); j++) {

                ComparableImage image2 = images.get(j);

                double similarity = compareImages(image1, image2);

                if (similarity >= MINIMUM_SIMILARITY) {
                    imagePairs.add(new ComparableImagePair(image1, image2, similarity));
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

    public static double compareImages(ComparableImage image1, ComparableImage image2) {
        double image1Proportion = (image1.getHeight() * 1.0)/(image1.getWidth() * 1.0);
        double image2Proportion = (image2.getHeight() * 1.0)/(image2.getWidth() * 1.0);

        double imagesProportion = image1Proportion/image2Proportion;
        final double LOWER_PROPORTION_DIFFERENCE = 1/MAXIMUM_PROPORTIONS_DIFFERENCE;
        if (!(LOWER_PROPORTION_DIFFERENCE <= imagesProportion && imagesProportion <= MAXIMUM_PROPORTIONS_DIFFERENCE)) {
            return 0;
        }

        double equality = 0;

        for (int x = 0; x < GENERATED_MINIATURE_SIZE; x++) {
            for (int y = 0; y < GENERATED_MINIATURE_SIZE; y++) {

                RGB rgb1 = new RGB(image1.getPreview().getRGB(x, y));
                RGB rgb2 = new RGB(image2.getPreview().getRGB(x, y));

                equality += rgb1.compareToRGB(rgb2);

            }
        }

        return equality / (GENERATED_MINIATURE_SIZE * GENERATED_MINIATURE_SIZE);
    }

    public boolean isInitialized() {
        return initialized;
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
