package universal.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Universal setting format for XIS
 */
public class UniversalSettingPackage {
    // String - name of a section.
    // ArrayList<Settings> - list of settings under that section
    private HashMap<String, ArrayList<Setting>> settingsMap;


    public UniversalSettingPackage () {
        settingsMap = new HashMap<>();
    }

    public void add (String sectionName, Setting setting) {

        if (settingsMap.containsKey(sectionName)) {
            ArrayList<Setting> settings = settingsMap.get(sectionName);
            settings.add(setting);
        } else {
            ArrayList<Setting> settings = new ArrayList<>();
            settings.add(setting);
            settingsMap.put(sectionName, settings);
        }

    }

    public boolean isOnList (String sectionName) {
        return settingsMap.containsKey(sectionName);
    }

    public ArrayList<Setting> getSettings (String sectionName) {
        return settingsMap.get(sectionName);
    }

    public Set<String> getKeys () {
        return settingsMap.keySet();
    }


}
