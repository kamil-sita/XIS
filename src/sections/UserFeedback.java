package sections;

import javafx.application.Platform;
import sections.main.MainViewController;

public final class UserFeedback {
    public static void reportProgress (double percentProgress) {
        Platform.runLater(() -> MainViewController.getProgressBar().setProgress(percentProgress));
    }

    public static void reportProgress (String message) {
        Platform.runLater(() -> MainViewController.setStatus(message));
    }
}
