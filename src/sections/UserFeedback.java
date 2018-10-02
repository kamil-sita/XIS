package sections;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import sections.main.MainViewController;


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
}
