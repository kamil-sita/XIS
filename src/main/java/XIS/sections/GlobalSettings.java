package XIS.sections;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalSettings {

    private static GlobalSettings globalSettingsInstance;

    public GlobalSettings() {

    }

    private int availableProcessors = -1;

    public int getNormalizedThreadCount() {
        if (availableProcessors == -1) {
            final int MAX_THREADS = Integer.MAX_VALUE;
            availableProcessors = Math.max(Math.min(Runtime.getRuntime().availableProcessors(), MAX_THREADS), 1);
        }
        return availableProcessors;
    }

    public ExecutorService getExecutorServiceForMostThreads() {
        return Executors.newFixedThreadPool(getNormalizedThreadCount());
    }

    public static GlobalSettings getInstance() {
        if (globalSettingsInstance == null) globalSettingsInstance = JsonLoader.getGlobalSettings();
        return globalSettingsInstance;
    }
}
