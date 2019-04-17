package XIS.toolset;

public class IntegerMath {
    /**
     * Returns floor(log(2,v)) for integer values.
     */
    public static int log2(int v) {
        int log = 0;
        while (v >= 2) {
            log++;
            v /= 2;
        }
        return log;
    }
}
