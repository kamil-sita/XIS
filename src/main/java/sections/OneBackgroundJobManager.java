package sections;

/**
 * Keeps track of background operations that take time, and tries to interrupt them if a new operation shows up. It is guaranteed
 * that if job is interrupted before it's finished, Manager won't call onUninterruptedFinish() method.
 */
public final class OneBackgroundJobManager {

    private static Interruptible currentJob = null;

    public static void setAndRunJob(Interruptible job) {
        interruptCurrentJobIfPossible();
        currentJob = job;
        new Thread(() -> {
            job.getRunnable().run();
            if (!job.isInterrupted()) {
                job.onUninterruptedFinish().run();
            }
        }).start();
    }

    public static void interruptCurrentJobIfPossible() {
        if (currentJob != null) {
            currentJob.interrupt();
            currentJob = null;
            UserFeedback.reportProgress(0);
        }
    }

}
