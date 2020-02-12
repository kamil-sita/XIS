package XIS.sections;

public final class MockInterruptible extends Interruptible {
    @Override
    public Runnable getRunnable() {
        return () -> {};
    }

    @Override
    public Runnable onUninterruptedFinish() {
        return () -> {};
    }

    @Override
    public void reportProgress(double percentProgress) {

    }

    @Override
    public void reportProgress (String message) {

    }

    @Override
    public void popup(String message) {

    }

}
