package XIS.sections;

import java.util.concurrent.Semaphore;

/**
 * Manages background operation that take time and attempts to make sure only one such operation exists. If a job
 * is interrupted before finishing its onUninterruptedFinish() method won't be called.
 */
public final class SingleJobManager {

    private static Interruptible currentJob = null;
    private static UserFeedback userFeedback;

    private static Semaphore threadAccess = new Semaphore(1);

    public static void setAndRunJob(Interruptible job) {
        try {
            threadAccess.acquire();
        } catch (InterruptedException e) {
            return;
        }
        attemptInterrupt();
        currentJob = job;
        new Thread(() -> {
            job.getRunnable().run();
            if (!job.isInterrupted()) {
                job.onUninterruptedFinish().run();
            }
        }).start();
        threadAccess.release();
    }

    public static void attemptInterrupt() {
        try {
            threadAccess.acquire();
        } catch (InterruptedException e) {
            return;
        }
        if (currentJob != null) {
            currentJob.interrupt();
            currentJob = null;
            if (userFeedback == null) userFeedback = new UserFeedback();
            userFeedback.reportProgress(0);
        }
        threadAccess.release();
    }

}
