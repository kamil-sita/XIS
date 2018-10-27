package sections;

/**
 * Interface to highlight methods that are interruptable (Thread)
 */
public interface Interruptable {
    Runnable getRunnable();
    Runnable onFinish();
}
