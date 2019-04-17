package XIS.sections;

/**
 * Simple interface for returning feedback that can be send to logger or displayed to user
 */
public interface Feedback {
    /**
     *
     * @param mainInformation information that should be reported
     * @param reporter class of reporting object (mainly this or ???.class)
     */
    public void report(String mainInformation, Class reporter);
}
