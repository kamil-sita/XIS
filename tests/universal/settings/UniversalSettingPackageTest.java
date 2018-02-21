package universal.settings;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UniversalSettingPackageTest {

    @Test
    void USPTest () {
        UniversalSettingPackage usp = new UniversalSettingPackage();

        usp.add("testSection", new Setting("setting1", 1));

        assertEquals(usp.getKeys().size(), 1);

        usp.add("testSection", new Setting("setting1", 2));

        assertEquals(usp.getKeys().size(), 1);

        Setting setting = new Setting("setting2", 3.123);

        usp.add("testSection2", setting);
        usp.add("testSection3", new Setting("setting3", 3.323));

        ArrayList<Setting> settings = usp.getSettings("testSection2");

        assertEquals(settings.size(), 1);
        assertEquals(settings.get(0), setting);
        assertEquals(usp.isOnList("testSection3"), true);
    }
}