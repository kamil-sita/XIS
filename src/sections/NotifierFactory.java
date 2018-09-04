package sections;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.awt.image.BufferedImage;

public class NotifierFactory {
    public static Notifier scalingImageNotifier(AnchorPane anchorPane, BufferedImage bufferedImage, ImageView imageView, int heighDiff) {
        System.out.println("new notifier request");
        Notifier notifier;
        notifier = (width, height) -> {
            System.out.println("notifier triggered!");
            anchorPane.setPrefWidth(width/2);
            anchorPane.setMaxWidth(width/2);

            if (bufferedImage == null) return;
            double ratio = (bufferedImage.getWidth() * 1.0)/(1.0 * bufferedImage.getHeight());

            double imgHeight = height - heighDiff;
            double maxWidth = (width-50)/2.0;

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
