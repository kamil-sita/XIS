package XIS.sections;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalSettings {

    private static GlobalSettings globalSettingsInstance;

    private int maxThreads = Integer.MAX_VALUE;

    public GlobalSettings() {

    }

    private int availableProcessors = -1;
    public int getNormalizedThreadCount() {
        if (availableProcessors == -1) {
            availableProcessors = Math.max(Math.min(Runtime.getRuntime().availableProcessors(), maxThreads), 1);
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
