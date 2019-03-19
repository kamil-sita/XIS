package XIS.sections.imagecopyfinder;

/**
 * Pair of comparable images
 */
public final class ComparableImagePair {

    private ComparableImage comparableImage1;
    private ComparableImage comparableImage2;
    private double similarity;

    public ComparableImagePair(ComparableImage comparableImage1, ComparableImage comparableImage2, double similarity) {
        this.comparableImage1 = comparableImage1;
        this.comparableImage2 = comparableImage2;
        this.similarity = similarity;
    }

    @Override
    public String toString() {
        return comparableImage1.getFile().getName() + ", " + comparableImage2.getFile().getName();
    }

    public boolean isInPair(ComparableImage cip) {
        return (cip.equals(comparableImage1)||cip.equals(comparableImage2));
    }

    public ComparableImage getComparableImageLeft() {
        return comparableImage1;
    }

    public ComparableImage getComparableImageRight() {
        return comparableImage2;
    }

    public double getSimilarity() {
        return similarity;
    }
}
