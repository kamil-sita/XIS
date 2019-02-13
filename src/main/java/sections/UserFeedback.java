package sections;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import sections.main.MainViewController;

import java.util.Optional;


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

    public static Optional<String> getText(String title, String header, String context) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(header);
        textInputDialog.setContentText(context);
        return textInputDialog.showAndWait();
    }


}
