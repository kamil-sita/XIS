package sections.imagecopyfinder;

import sections.GlobalSettings;
import sections.Interruptible;
import toolset.imagetools.Rgb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
     * @return true if initialized
     */
    public boolean run(List<GroupedFolder> groupedFolders, Interruptible interruptible) {
        this.interruptible = interruptible;
        var optionalImages = ComparableImageIO.loadFiles(groupedFolders, generatedMiniatureSize, interruptible);
        if (interruptible.isInterrupted()) return false;
        if (!optionalImages.isEmpty()) {
            images = optionalImages;
            findPairsMultithreaded();
            return true;
        }
        interruptible.reportProgress("No images found");
        return false;
    }


    /**
     * Finds pairs of similar images using multithreading.
     * Instead of comparing every element of array with every other element, it sorts them first and for every element compares it until they're not similar enough (to be exact
     * it compares their H element in average HSB.
     * Instead of O(n^2) it is O(nlogn) (sorting) + k * n, where k << n
     */
    private void findPairsMultithreaded() {
        imagePairs = Collections.synchronizedList(new ArrayList<>());

        var startTime = System.nanoTime();

        ExecutorService exec = Executors.newFixedThreadPool(GlobalSettings.getThreadCount());
        AtomicInteger finishedThreads = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(images.size());

        interruptible.reportProgress("Sorting");
        images.sort(Comparator.comparingDouble(o -> o.getAverageHsb().H));
        interruptible.reportProgress("Sorted");

        try {
            for (int i = 0; i < images.size(); i++) {
                final int c = i;
                exec.submit(() -> {
                    findPairsFor(c);
                    finishedThreads.getAndIncrement();
                    reportFindPairingProgress(startTime, finishedThreads.getAcquire(), images.size());
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

        interruptible.reportProgress("Images compared");
    }

    private void findPairsFor(int index) {
        final double HUE_DIFF_MAX = 0.2;
        //comparing with all other images until hues differ enough
        double hForIndex = images.get(index).getAverageHsb().H;
        for (int i = 1; i < images.size(); i++) {
            int compareIndex = (i + index)%images.size();

            if (Math.abs(images.get(compareIndex).getAverageHsb().H - hForIndex) > HUE_DIFF_MAX) {
                System.out.println(i);
                return;
            }
            addPairIfSimilar(images.get(index), images.get(compareIndex));
        }
    }


    private void addPairIfSimilar(ComparableImage image1, ComparableImage image2) {
        if (!image1.canCompare(image2)) return;
        if (image1.getAverageHsb().hueDiff(image2.getAverageHsb()) <= MAXIMUM_HUE_DIFFERENCE) {
            double similarity = compareImages(image1, image2);
            if (similarity >= MINIMUM_SIMILARITY) {
                imagePairs.add(new ComparableImagePair(image1, image2, similarity));
            }
        }
    }

    private void reportFindPairingProgress(long startTime, int i, int all) {
        if (i >= 10) {
            interruptible.reportProgress("Comparing images (" + (i+1) + "/" + images.size() + "). Estimated time left for comparing: " + getApproximateTimeLeftLinear(i, startTime, all - i) + " seconds.");
        } else {
            interruptible.reportProgress("Comparing images (" + (i+1) + "/" + images.size() + ")");
        }

        interruptible.reportProgress((1.0*i)/images.size());
    }

    private static double getApproximateTimeLeftLinear(int currentIteration, long time, int left) {
        double dt = System.nanoTime() - time;
        dt = dt * (left) / (currentIteration);
        dt /= 1000000000;
        return dt;
    }



    /**
     * Calculates equality % of two images, based on RGB>compareToRGB() method
     * @param image1
     * @param image2
     * @return % of similarity between images.
     */

    public double compareImages(ComparableImage image1, ComparableImage image2) {
        final double POWER = 2.35;

        if (!areProportionsAcceptable(image1, image2)) return -1;

        double equality = 0;

        for (int x = 0; x < generatedMiniatureSize; x++) {
            for (int y = 0; y < generatedMiniatureSize; y++) {

                Rgb rgb1 = new Rgb(image1.getPreview().getRGB(x, y));
                Rgb rgb2 = new Rgb(image2.getPreview().getRGB(x, y));

                equality += Math.pow(1 - rgb1.compareToRGB(rgb2), POWER);

            }
        }

        return 1 - Math.pow(equality/(generatedMiniatureSize * generatedMiniatureSize), 1/POWER);
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
        SUCCESSFUL, NO_IMAGES_IN_DIRECTORY, NOT_FOLDER, IO_ERROR, NO_PAIRS, ERROR_ALREADY_HANDLED;

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
