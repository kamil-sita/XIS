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
        //we want for hues like 0,00 and 0,999 to appear close to each other (diff should be 0,001, not 0,999)
        //for this formula: (0,000 - 0,999 + 0,5) % 1 - 0,5
        //(-0,499) % 1 - 0,5
        //-0,001
        //now we only need ABS function to fix the fact that result is lower than 0

        //this formula is derived from formula for difference of two angles
        return Math.abs(((H - otherHsb.H + 0.5) % 1) - 0.5);
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
