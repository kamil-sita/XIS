package XIS.sections;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for job interruption and progress feedback. If you don't need neither use MockInterruptible class.
 */
public abstract class Interruptible {
    private boolean isInterrupted = false;
    private UserFeedback userFeedback;
    private List<Runnable> listeners = new ArrayList<>();

    public abstract Runnable getRunnable();
    public abstract Runnable onUninterruptedFinish();

    public Interruptible() {
        userFeedback = UserFeedback.getInstance();
    }

    public final boolean isInterrupted() {
        return isInterrupted;
    }

    protected final void interrupt() {
        isInterrupted = true;
        for (var listener : listeners) {
            listener.run();
        }
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

    public final UserFeedback getUserFeedback() {
        return userFeedback;
    }

    public final void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public final void removeListener(Runnable listener) {
        listeners.remove(listener);
    }
}
