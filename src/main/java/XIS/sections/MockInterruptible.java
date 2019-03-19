package XIS.sections;

public final class MockInterruptible extends Interruptible {
    @Override
    public Runnable getRunnable() {
        return null;
    }

    @Override
    public Runnable onUninterruptedFinish() {
        return null;
    }

    public boolean isInterrupted() {
        return false;
    }

    public void interrupt() {

    }

    public void reportProgress(double percentProgress) {
    }

    public void reportProgress (String message) {
    }

    public void popup(String message) {
    }

}
