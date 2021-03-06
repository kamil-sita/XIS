package XIS.toolset.scanfilters;

public class QuantizationFilterArguments implements Arguments {
    private boolean filterBackground;
    private boolean scaleBrightness;
    private int colorCount;
    private double brightnessDifference;
    private double saturationDifference;
    private boolean inverted;

    public boolean isFilterBackground() {
        return filterBackground;
    }

    public void setFilterBackground(boolean filterBackground) {
        this.filterBackground = filterBackground;
    }

    public boolean isScaleBrightness() {
        return scaleBrightness;
    }

    public void setScaleBrightness(boolean scaleBrightness) {
        this.scaleBrightness = scaleBrightness;
    }

    public int getColorCount() {
        return colorCount;
    }

    public void setColorCount(int colorCount) {
        this.colorCount = colorCount;
    }

    public double getBrightnessDifference() {
        return brightnessDifference;
    }

    public void setBrightnessDifference(double brightnessDifference) {
        this.brightnessDifference = brightnessDifference;
    }

    public double getSaturationDifference() {
        return saturationDifference;
    }

    public void setSaturationDifference(double saturationDifference) {
        this.saturationDifference = saturationDifference;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public boolean isInverted() {
        return inverted;
    }
}
