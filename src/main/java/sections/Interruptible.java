package sections;

/**
 *
 */
public abstract class Interruptible {
    protected boolean isInterrupted = false;

    public abstract Runnable getRunnable();
    public abstract Runnable onUninterruptedFinish();

    public final boolean isInterrupted() {
        return isInterrupted;
    }

    public final void interrupt() {
        isInterrupted = true;
    }
}
