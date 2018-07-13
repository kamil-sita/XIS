package universal.settings;

public final class Setting {

    private String setting; //name of setting

    private DefaultArgument defaultArgument;

    private int intArgument;
    private double doubleArgument;
    private String stringArgument;

    public Setting (String setting, String argument) {
        this.setting = setting;
        stringArgument = argument;
        defaultArgument = DefaultArgument.stringArg;
    }

    public Setting (String setting, int argument) {
        this.setting = setting;
        intArgument = argument;
        defaultArgument = DefaultArgument.intArg;
    }

    public Setting (String setting, double argument) {
        this.setting = setting;
        doubleArgument = argument;
        defaultArgument = DefaultArgument.doubleArg;
    }

    // getters

    public DefaultArgument getArgumentType() {
        return defaultArgument;
    }

    public String getSetting() {
        return setting;
    }

    public int getIntArgument() {
        return intArgument;
    }

    public double getDoubleArgument() {
        return doubleArgument;
    }

    public String getStringArgument() {
        return stringArgument;
    }

    public String getArgumentAsString() {
        switch (defaultArgument) {
            case intArg:
                return "" + getIntArgument();
            case doubleArg:
                return "" + getDoubleArgument();
            case stringArg:
                return getStringArgument();
            case none:
                return "";
        }
        return "";
    }

    // setters

    public void setIntArgument (int value) {
        intArgument = value;
        defaultArgument = DefaultArgument.intArg;
    }

    public void setDoubleArgument (double value) {
        doubleArgument = value;
        defaultArgument = DefaultArgument.doubleArg;
    }

    public void setStringArgument (String value) {
        stringArgument = value;
        defaultArgument = DefaultArgument.stringArg;
    }

    // enum

    public enum DefaultArgument {
        intArg, doubleArg, stringArg, none;
    }

}
