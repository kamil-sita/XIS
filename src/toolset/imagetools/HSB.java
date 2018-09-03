package toolset.imagetools;

import java.awt.*;

public class HSB {
    public double H;
    public double S;
    public double B;

    public HSB() {

    }

    public HSB(double H, double S, double B) {
        this.H = H;
        this.S = S;
        this.B = B;
    }

    public double hueDiff(HSB otherHsb) {
        return Math.abs(H - otherHsb.H);
    }

    public double saturationDiff(HSB otherHsb) {
        return Math.abs(S - otherHsb.S);
    }

    public double brightnessDiff(HSB otherHsb) {
        return Math.abs(B - otherHsb.B);
    }

    public RGB toRGB() {
        return new RGB(Color.HSBtoRGB((float) H, (float) S, (float) B));
    }

}
