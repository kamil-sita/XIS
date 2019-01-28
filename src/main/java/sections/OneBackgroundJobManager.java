package sections;

/**
 * Keeps track of background operations that take time, and tries to interrupt them if a new operation shows up. It is guaranteed
 * that if job is interrupted before it's finished, Manager won't call onUninterruptedFinish() method.
 */
public final class OneBackgroundJobManager {

    private static Interruptable currentJob = null;

    public static void setAndRunJob(Interruptable job) {
        interruptIfPossible();
        currentJob = job;
        new Thread(() -> {
            job.getRunnable().run();
            if (!Thread.currentThread().isInterrupted()) {
                job.onUninterruptedFinish().run();
            }
        }).start();
    }

    public static void interruptIfPossible() {
        if (currentJob != null) {
            currentJob.interrupt();
            currentJob = null;
            UserFeedback.reportProgress(0);
        }
    }

}
