package XIS;

import XIS.sections.GlobalSettings;

public final class Main  {

    public static void main(String[] args) {
        GlobalSettings.getInstance(); //initializing global settings
        WindowStart.runWindow();

    }
}
