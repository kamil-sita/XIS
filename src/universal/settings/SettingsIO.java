package universal.settings;

import universal.tools.BufferedWriterTools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public final class SettingsIO {
    /**
     * Saves settings to given file
     * @param usp settings
     * @param loc location
     */

    public void write (UniversalSettingPackage usp, String loc) {
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(loc));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String[] keys = usp.getKeys().toArray(new String[usp.getKeys().size()]);

        for (String key : keys) {
            BufferedWriterTools.writeIgnoreExceptions(bw, "[" + key + "]");
            BufferedWriterTools.writeNewLine(bw);
            for (Setting setting : usp.getSettings(key)) {
                BufferedWriterTools.writeIgnoreExceptions(bw, setting.getSetting());
                switch (setting.getArgumentType()) {
                    case none:
                        break;
                    default:
                        BufferedWriterTools.writeIgnoreExceptions(bw, " = ");
                        BufferedWriterTools.writeIgnoreExceptions(bw, setting.getArgumentAsString());
                        break;
                }
                BufferedWriterTools.writeNewLine(bw);
            }

        }

        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
