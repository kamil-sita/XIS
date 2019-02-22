package sections.imagecopyfinder;

import sections.Interruptible;
import sections.UserFeedback;
import toolset.imagetools.Rgb;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ImageComparator compares images and for images that have more than MINIMUM_SIMILARITY similarity, outputs them into list of ComparableImagePairs
 */
public final class ImageComparator {

    //Minimum similarity of hues for given pair to be considered similar:
    private static final double MAXIMUM_HUE_DIFFERENCE = 0.1;
    //Minimum similarity for given pair to even be considered similar
    private static final double MINIMUM_SIMILARITY = 0.79;
    //Size of generated miniature of image
    private int generatedMiniatureSize;

    private List<ComparableImage> images;
    private List<ComparableImagePair> imagePairs;

    private ImageComparatorStatus status;

    private Interruptible interruptible;


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
    public boolean initialize(File[] folders, boolean isGeometricalMode, Interruptible interruptible, boolean alternativeMode) {
        this.interruptible = interruptible;
        var optionalImages = ComparableImageIO.loadFiles(folders, generatedMiniatureSize, interruptible, alternativeMode);
        if (interruptible.isInterrupted()) return false;
        if (!optionalImages.isEmpty()) {
            images = optionalImages;
            findPairsMultithreaded(isGeometricalMode);
            return true;
        }
        UserFeedback.reportProgress("No images found");
        return false;
    }


    /**
     * Finds pairs of similar images using all of available cores.
     * @param geometricalMode
     */
    private void findPairsMultithreaded(boolean geometricalMode) {
        imagePairs = Collections.synchronizedList(new ArrayList<>());

        var clock = new ImageComparatorClock(images.size());

        final int AVAILABLE_THREADS = Runtime.getRuntime().availableProcessors();
        ExecutorService exec = Executors.newFixedThreadPool(AVAILABLE_THREADS);
        AtomicInteger finishedThreads = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(images.size());

        try {
            for (int i = 0; i < images.size(); i++) {
                final int c = i;
                exec.submit(() -> {
                    findPairsFor(c, geometricalMode);
                    finishedThreads.getAndIncrement();
                    reportFindPairingProgress(clock, finishedThreads.getAcquire());
                    latch.countDown();
                });
            }
        } finally {
            exec.shutdown();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        UserFeedback.reportProgress("Images compared");
    }

    private void findPairsFor(int i, boolean geometricalMode) {
        ComparableImage image1 = images.get(i);
        for (int j = i + 1; j < images.size(); j++) {
            if (interruptible.isInterrupted()) return;
            ComparableImage image2 = images.get(j);
            addPairIfSimilar(geometricalMode, image1, image2);
        }
    }

    private void addPairIfSimilar(boolean geometricalMode, ComparableImage image1, ComparableImage image2) {
        if (image1.getHsb().hueDiff(image2.getHsb()) <= MAXIMUM_HUE_DIFFERENCE) {
            double similarity = compareImages(image1, image2, geometricalMode);
            if (similarity >= MINIMUM_SIMILARITY) {
                imagePairs.add(new ComparableImagePair(image1, image2, similarity));
            }
        }
    }

    private void reportFindPairingProgress(ImageComparatorClock clock, int i) {
        if (i >= 10) {
            double dt = clock.getApproximateTimeLeftComparing(i);
            UserFeedback.reportProgress("Comparing images (" + (i+1) + "/" + images.size() + "). Estimated time left for comparing: " + ((int) (dt)) + " seconds.");
        } else {
            UserFeedback.reportProgress("Comparing images (" + (i+1) + "/" + images.size() + ")");
        }

        UserFeedback.reportProgress((1.0*i)/images.size());
    }




    /**
     * Calculates equality % of two images, based on RGB>compareToRGB() method
     * @param image1
     * @param image2
     * @return % of similarity between images.
     */

    public double compareImages(ComparableImage image1, ComparableImage image2, boolean alternativeMode) {
        final double POWER = 2.35;

        if (!areProportionsAcceptable(image1, image2)) return -1;

        double equality = 0;

        for (int x = 0; x < generatedMiniatureSize; x++) {
            for (int y = 0; y < generatedMiniatureSize; y++) {

                Rgb rgb1 = new Rgb(image1.getPreview().getRGB(x, y));
                Rgb rgb2 = new Rgb(image2.getPreview().getRGB(x, y));

                if (alternativeMode) {
                    equality += Math.pow(1 - rgb1.compareToRGB(rgb2), POWER);
                } else {
                    equality += rgb1.compareToRGB(rgb2);
                }

            }
        }

        if (alternativeMode) {
            return 1 - Math.pow(equality/(generatedMiniatureSize * generatedMiniatureSize), 1/POWER);
        } else {
            return equality / (generatedMiniatureSize * generatedMiniatureSize);
        }
    }

    private boolean areProportionsAcceptable(ComparableImage image1, ComparableImage image2) {
        double imagesProportionRatio = image1.getProportion()/image2.getProportion();

        final double MAXIMUM_PROPORTIONS_DIFFERENCE = 1.1;
        final double MINIMUM_PROPORTIONS_DIFFERENCE = 1.0/MAXIMUM_PROPORTIONS_DIFFERENCE;
        return MINIMUM_PROPORTIONS_DIFFERENCE <= imagesProportionRatio && imagesProportionRatio <= MAXIMUM_PROPORTIONS_DIFFERENCE;
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
