package XIS.toolset.scanfilters;

public class HighPassFilterArguments implements Arguments {
    private int blurPasses;
    private double scaleBrightnessVal;
    boolean blackAndWhite;

    public int getBlurPasses() {
        return blurPasses;
    }

    public void setBlurPasses(int blurPasses) {
        this.blurPasses = blurPasses;
    }
    public double getScaleBrightnessVal() {
        return scaleBrightnessVal;
    }

    public void setScaleBrightnessVal(double scaleBrightnessVal) {
        this.scaleBrightnessVal = scaleBrightnessVal;
    }

    public boolean isBlackAndWhite() {
        return blackAndWhite;
    }

    public void setBlackAndWhite(boolean blackAndWhite) {
        this.blackAndWhite = blackAndWhite;
    }
}
