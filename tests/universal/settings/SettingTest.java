package universal.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SettingTest {
    @Test
    void SettingTest() {
        //constructors
        Setting settingInt = new Setting("set", 2);
        Setting settingDouble = new Setting("set", 54.5);
        Setting settingString = new Setting("set", "test");

        //normal
        assertEquals(settingInt.getIntArgument(), 2);
        assertEquals(settingDouble.getDoubleArgument(), 54.5);
        assertEquals(settingString.getStringArgument().equals("test"), true);
        assertEquals(settingInt.getSetting().equals("set"), true);

        //as string
        assertEquals(settingInt.getArgumentAsString().equals("2"), true);
        assertEquals(settingDouble.getArgumentAsString().equals("54.5"), true);
        assertEquals(settingString.getArgumentAsString().equals("test"), true);

        //default value
        assertEquals(settingInt.getArgumentType(), Setting.DefaultArgument.intArg);
        assertEquals(settingDouble.getArgumentType(), Setting.DefaultArgument.doubleArg);
        assertEquals(settingString.getArgumentType(), Setting.DefaultArgument.stringArg);

        //changed value
        Setting changedValue = new Setting("set", 3);

        changedValue.setDoubleArgument(3.0);
        assertEquals(changedValue.getArgumentType(), Setting.DefaultArgument.doubleArg);

        changedValue.setIntArgument(3);
        assertEquals(changedValue.getArgumentType(), Setting.DefaultArgument.intArg);

        changedValue.setStringArgument("3");
        assertEquals(changedValue.getArgumentType(), Setting.DefaultArgument.stringArg);


    }
}