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

    public String toString() {
        return comparableImage1.getFile().getName() + ", " + comparableImage2.getFile().getName();
    }

    public boolean isInPair(ComparableImage cip) {
        return (cip.equals(comparableImage1)||cip.equals(comparableImage2));
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
