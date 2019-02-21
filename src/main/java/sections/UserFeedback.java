package sections;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import sections.main.MainViewController;
import toolset.io.BufferedImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public final class UserFeedback {

    public static void reportProgress (double percentProgress) {
        Platform.runLater(() -> MainViewController.getProgressBar().setProgress(percentProgress));
    }

    public static void reportProgress (String message) {
        Platform.runLater(() -> MainViewController.setStatus(message));
    }

    public static void popup(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.CLOSE);
            alert.show();
        });

    }

    public static void openInDefault(BufferedImage bufferedImage) {
        var file = BufferedImageIO.saveImage(bufferedImage, null);
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
