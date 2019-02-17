package toolset;

public class IntegerMath {
    public static int log2(int v) {
        int log = 0;
        if ((v & 0xffff0000) != 0) {
            v >>>= 16;
            log = 16;
        }
        if (v >= 256) {
            v >>>= 8;
            log += 8;
        }
        if (v >= 16) {
            v >>>= 4;
            log += 4;
        }
        if (v >= 4) {
            v >>>= 2;
            log += 2;
        }
        return log + (v >>> 1);
    }
}
