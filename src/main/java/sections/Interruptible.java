package sections;

/**
 * Abstract class for job interruption and JavaFX progress feedback.
 */
public abstract class Interruptible {
    protected boolean isInterrupted = false;

    public abstract Runnable getRunnable();
    public abstract Runnable onUninterruptedFinish();

    public boolean isInterrupted() {
        return isInterrupted;
    }

    public void interrupt() {
        isInterrupted = true;
    }

    public void reportProgress(double percentProgress) {
        UserFeedback.reportProgress(percentProgress);
    }

    public void reportProgress (String message) {
        UserFeedback.reportProgress(message);
    }

    public void popup(String message) {
        UserFeedback.reportProgress(message);
    }
}
