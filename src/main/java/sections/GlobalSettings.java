package sections;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalSettings {

    private static GlobalSettings globalSettingsInstance;

    private int maxThreads = Integer.MAX_VALUE;

    public GlobalSettings() {

    }

    private int getThreadCount() {
        return Math.max(Math.min(Runtime.getRuntime().availableProcessors(), maxThreads), 1);
    }

    public ExecutorService getExecutorServiceForMostThreads() {
        return Executors.newFixedThreadPool(getThreadCount());
    }

    public static GlobalSettings getInstance() {
        if (globalSettingsInstance == null) globalSettingsInstance = JsonLoader.getGlobalSettings();
        return globalSettingsInstance;
    }
}
