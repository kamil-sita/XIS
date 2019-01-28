package sections;

/**
 *
 */
public abstract class Interruptable {
    protected boolean isInterrupted = false;

    public abstract Runnable getRunnable();
    public abstract Runnable onUninterruptedFinish();

    public final boolean isInterrupted() {
        return isInterrupted;
    }

    public final void interrupt() {
        System.out.println("interrupted!");
        isInterrupted = true;
    }
}
