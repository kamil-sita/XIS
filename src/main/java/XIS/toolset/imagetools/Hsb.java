package XIS.toolset.imagetools;

import java.awt.*;

public class Hsb {
    public double H;
    public double S;
    public double B;

    public Hsb() {

    }

    public Hsb(double H, double S, double B) {
        this.H = H;
        this.S = S;
        this.B = B;
    }

    public double hueDiff(Hsb otherHsb) {
        return Math.abs(H - otherHsb.H);
    }

    public double saturationDiff(Hsb otherHsb) {
        return Math.abs(S - otherHsb.S);
    }

    public double brightnessDiff(Hsb otherHsb) {
        return Math.abs(B - otherHsb.B);
    }

    public Rgb toRGB() {
        return new Rgb(Color.HSBtoRGB((float) H, (float) S, (float) B));
    }

    @Override
    public String toString() {
        return "HSB: h: " + H + ", s: " + S + ", b: " + B;
    }

}
