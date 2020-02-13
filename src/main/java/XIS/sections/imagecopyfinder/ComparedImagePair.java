package XIS.sections.imagecopyfinder;

import java.io.Serializable;

/**
 * Pair of compared images images
 */
public final class ComparedImagePair implements Serializable {

    private ComparedImage image1;
    private ComparedImage image2;
    private double similarity;

    public ComparedImagePair(ComparedImage image1, ComparedImage image2, double similarity) {
        this.image1 = image1;
        this.image2 = image2;
        this.similarity = similarity;
    }

    @Override
    public String toString() {
        return image1.getFile().getName() + ", " + image2.getFile().getName();
    }

    public boolean isInPair(ComparedImage cip) {
        return (cip.equals(image1)||cip.equals(image2));
    }

    public ComparedImage getImageOnLeft() {
        return image1;
    }

    public ComparedImage getImageOnRight() {
        return image2;
    }

    public double getSimilarity() {
        return similarity;
    }
}
