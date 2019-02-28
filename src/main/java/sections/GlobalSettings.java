package sections;

public class GlobalSettings {
    private static int MAX_THREADS = 3;

    public static int getThreadCount() {
        return Math.min(Runtime.getRuntime().availableProcessors(), MAX_THREADS);
    }
}
