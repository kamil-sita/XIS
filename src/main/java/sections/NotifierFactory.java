package sections;

import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.awt.image.BufferedImage;

public class NotifierFactory {

    /**
     * Scales image size on every window size to ensure fluid experience.
     * @param bufferedImage input image (to calculate width/height ratio)
     * @param imageView imageView that will be set to given width and height
     * @param heightDiff amount of space that should be left for other components vertically (window height - image height)
     * @param widthDiff amount of space that should be left for other components horizontally (window width - image width)
     * @param horizontalScaling % of horizontal space that this image should cover (normally 100%, in case of 2 images 50% ...)
     * @return new notifier created with those arguments in mind
     */
    public static Notifier scalingImageNotifier(BufferedImage bufferedImage, ImageView imageView, int heightDiff, int widthDiff, double horizontalScaling, SplitPane split) {
        return (width, height) -> {
            if (bufferedImage == null) return;
            double ratio = (bufferedImage.getWidth() * 1.0)/(1.0 * bufferedImage.getHeight());

            double imgHeight = height - heightDiff;
            double maxWidth = 0;
            if (split != null) {
                maxWidth = (width-widthDiff) * horizontalScaling * (1 - split.getDividerPositions()[0]);
            } else {
                maxWidth = (width-widthDiff) * horizontalScaling;
            }

            double imgWidth = imgHeight * ratio;

            if (imgWidth > maxWidth) {
                ratio = maxWidth / imgWidth;
                imgWidth *= ratio;
                imgHeight *= ratio;
            }

            imgWidth = imgWidth < 1 ? 1 : imgWidth;
            imgHeight = imgHeight < 1 ? 1 : imgHeight;

            System.out.println("W: " + imgWidth);
            System.out.println("H: " + imgHeight);

            imageView.setFitWidth(imgWidth);
            imageView.setFitHeight(imgHeight);
        };
    }

    public static Notifier scalingImageNotifier(BufferedImage bufferedImage, ImageView imageView, int heightDiff, int widthDiff, double horizontalScaling) {
        return scalingImageNotifier(bufferedImage, imageView, heightDiff, widthDiff, horizontalScaling, null);
    }

    public static Notifier scalingNotifier(Pane anchorPane, double horizontalScaling) {
        return new Notifier() {
            @Override
            public void notify(double width, double height) {
                System.out.println("scaling: " + width);
                System.out.println("setting to: " + width * horizontalScaling);
                anchorPane.maxWidth(width * horizontalScaling);
                anchorPane.prefWidth(width * horizontalScaling);
                System.out.println(anchorPane.getMaxWidth());
                System.out.println(anchorPane.getPrefWidth());
            }
        };
    }

    public static Notifier simpleScaleNotifier(Pane pane, double horizontalScaling, double verticalScaling) {
        return (width, height) -> {
            pane.setMinWidth(width * horizontalScaling);
            pane.setPrefWidth(width * horizontalScaling);
            pane.setMaxWidth(width * horizontalScaling);
            pane.setMinHeight(height * verticalScaling);
            pane.setPrefHeight(height * verticalScaling);
            pane.setMaxHeight(height * verticalScaling);
        };
    }
}
