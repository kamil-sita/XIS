package sections.imagecompetition;

import toolset.imagetools.BufferedImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageContestant {
    private File image;
    private double rating = RatingSystem.DEFAULT_RATING;
    private int ratings = 0;

    public ImageContestant(File image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return BufferedImageIO.getImage(image).get();
    }

    public int getRatings() {
        return ratings;
    }

    public void updateRatings() {
        ratings++;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getName() {
        return image.getName();
    }

    public static List<ImageContestant> createImageContestants(List<File> imageFiles) {
        List<ImageContestant> imageContestants = new ArrayList<>();
        for (File file : imageFiles) {
            imageContestants.add(new ImageContestant(file));
        }
        return imageContestants;
    }
}
