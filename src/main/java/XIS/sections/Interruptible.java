package XIS.sections;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Abstract class for job interruption and JavaFX progress feedback.
 */
public abstract class Interruptible {
    protected boolean isInterrupted = false;
    private UserFeedback userFeedback;

    public abstract Runnable getRunnable();
    public abstract Runnable onUninterruptedFinish();

    public Interruptible() {
        userFeedback = UserFeedback.getInstance();
    }

    public boolean isInterrupted() {
        return isInterrupted;
    }

    public void interrupt() {
        isInterrupted = true;
    }

    public void reportProgress(double percentProgress) {
        userFeedback.reportProgress(percentProgress);
    }

    public void reportProgress (String message) {
        userFeedback.reportProgress(message);
    }

    public void popup(String message) {
        userFeedback.reportProgress(message);
    }

    public void openInDefault(File file) {
        userFeedback.openInDefault(file);
    }

    public void openInDefault(BufferedImage bufferedImage) {
        userFeedback.openInDefault(bufferedImage);
    }

    public UserFeedback getUserFeedback() {
        return userFeedback;
    }
}
