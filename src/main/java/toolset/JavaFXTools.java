package toolset;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sections.UserFeedback;
import toolset.imagetools.BufferedImageScale;

import java.awt.image.BufferedImage;

public class JavaFXTools {

    public static void showPreview(BufferedImage previewImage, boolean higherQuality, ImageView imageView, final SetImageDelegate delegate) {
        if (higherQuality) {
            Platform.runLater(() -> delegate.set(previewImage));
            UserFeedback.reportProgress("Generated preview. Scaling to fit window...");
            double width = imageView.getFitWidth();
            double height = imageView.getFitHeight();
            if (width < previewImage.getWidth()) {
                Platform.runLater(() -> delegate.set(previewImage));
                UserFeedback.reportProgress("Generated scaled preview.");
            } else {
                var scaledOutput = BufferedImageScale.getHighQualityScaledImage(previewImage, width, height);
                Platform.runLater(() -> delegate.set(scaledOutput));
                UserFeedback.reportProgress("Generated scaled preview.");
            }
        } else {
            Platform.runLater(() -> delegate.set(previewImage));
            UserFeedback.reportProgress("Generated preview.");
        }
    }

    public static Image toFxImage(BufferedImage bi) {
        return SwingFXUtils.toFXImage(bi, null);
    }

    public interface SetImageDelegate {
        void set(BufferedImage bufferedImage);
    }

}
