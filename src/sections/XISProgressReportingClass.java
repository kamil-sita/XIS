package sections;

import javafx.application.Platform;
import sections.main.MainViewController;

public abstract class XISProgressReportingClass {
    protected void reportProgress (double percentProgress) {
        Platform.runLater(() -> MainViewController.getProgressBar().setProgress(percentProgress));
    }

    protected void reportProgress (String message) {
        Platform.runLater(() -> MainViewController.setStatus(message));
    }
}
