package XIS.sections;

/**
 * Manages background operation that take time and attempts to make sure only one such operation exists. If a job
 * is interrupted before finishing its onUninterruptedFinish() method won't be called.
 */
public final class SingleJobManager {

    private static Interruptible currentJob = null;
    private static UserFeedback userFeedback;

    public static void setAndRunJob(Interruptible job) {
        attemptInterrupt();
        currentJob = job;
        new Thread(() -> {
            job.getRunnable().run();
            if (!job.isInterrupted()) {
                job.onUninterruptedFinish().run();
            }
        }).start();
    }

    public static void attemptInterrupt() {
        if (currentJob != null) {
            currentJob.interrupt();
            currentJob = null;
            if (userFeedback == null) userFeedback = new UserFeedback();
            userFeedback.reportProgress(0);
        }
    }

}
