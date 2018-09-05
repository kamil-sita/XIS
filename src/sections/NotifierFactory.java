package sections;

import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class NotifierFactory {
    public static Notifier scalingImageNotifier(BufferedImage bufferedImage, ImageView imageView, int heighDiff, int widthDiff, double scaling) {
        Notifier notifier;
        notifier = (width, height) -> {

            if (bufferedImage == null) return;
            double ratio = (bufferedImage.getWidth() * 1.0)/(1.0 * bufferedImage.getHeight());

            double imgHeight = height - heighDiff;
            double maxWidth = (width-widthDiff)/scaling;

            double imgWidth = imgHeight * ratio;

            if (imgWidth > maxWidth) {
                ratio = maxWidth / imgWidth;
                imgWidth *= ratio;
                imgHeight *= ratio;
            }

            imageView.setFitWidth(imgWidth);
            imageView.setFitHeight(imgHeight);
        };
        return notifier;
    }
}
