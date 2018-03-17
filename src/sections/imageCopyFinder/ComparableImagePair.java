package sections.imageCopyFinder;

public class ComparableImagePair {

    private ComparableImage comparableImage1;
    private ComparableImage comparableImage2;
    private double similarity;

    public ComparableImagePair(ComparableImage comparableImage1, ComparableImage comparableImage2, double similarity) {
        this.comparableImage1 = comparableImage1;
        this.comparableImage2 = comparableImage2;
        this.similarity = similarity;
    }

    public ComparableImagePair(ComparableImage comparableImage1, ComparableImage comparableImage2) {
        this.comparableImage1 = comparableImage1;
        this.comparableImage2 = comparableImage2;
        similarity = ImageComparator.compareImages(comparableImage1, comparableImage2);
    }

    public ComparableImage getComparableImage1() {
        return comparableImage1;
    }

    public ComparableImage getComparableImage2() {
        return comparableImage2;
    }

    public double getSimilarity() {
        return similarity;
    }
}
