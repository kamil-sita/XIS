package sections;

/**
 * Keeps track of background operations that take time, and tries to interrupt them if a new operation shows up. It is guaranteed
 * that if job is interrupted before it's finished, Manager won't call onFinish() method.
 */
public final class OneBackgroundJobManager {

    private static Thread currentJob = null;

    public static void setAndRunJob(Interruptable job) {
        interruptIfPossible();
        currentJob = new Thread(() -> {
            job.getRunnable().run();
            if (!Thread.currentThread().isInterrupted()) {
                job.onFinish().run();
            }
        });
        currentJob.start();
    }

    public static void interruptIfPossible() {
        try {
            if (currentJob != null) {
                currentJob.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
