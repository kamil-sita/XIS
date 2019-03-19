package XIS.sections;

import XIS.toolset.io.StringIO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class JsonLoader {

    private static String settingsFile = "XIS.conf";

    public static GlobalSettings getGlobalSettings() {
        Gson gson = new Gson();
        System.out.println("Loading settings file...");
        var string = StringIO.getString(settingsFile);
        if (string == null || string.trim().length() == 0) {
            generateNewGlobalSettings();
        }
        try {
            return gson.fromJson(StringIO.getString(settingsFile), GlobalSettings.class);
        } catch (Exception e) {
            System.out.println("Failed to read! Generating new file");
            generateNewGlobalSettings();
            try {
                return gson.fromJson(StringIO.getString(settingsFile), GlobalSettings.class);
            } catch (Exception f) {
                f.printStackTrace();
                System.exit(0);
            }
        }
        return null;
    }

    private static void generateNewGlobalSettings() {
        System.out.println("Generating new settings file");
        GlobalSettings globalSettings = new GlobalSettings();
        saveGlobalSettings(globalSettings);
    }

    public static void saveGlobalSettings(GlobalSettings globalSettings) {
        System.out.println("Saving settings file...");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(globalSettings);
        StringIO.saveString(json, new File(settingsFile));
    }

}
