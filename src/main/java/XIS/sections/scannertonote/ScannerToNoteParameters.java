package XIS.sections.scannertonote;

import java.awt.image.BufferedImage;

public class ScannerToNoteParameters {
    private final BufferedImage input;
    private final boolean filterBackground;
    private final boolean scaleBrightness;
    private final int colors;
    private final double brightnessDiff;
    private final double saturationDiff;

    public ScannerToNoteParameters(BufferedImage input, boolean filterBackground, boolean scaleBrightness, int colors, double brightnessDiff, double saturationDiff) {
        this.input = input;
        this.filterBackground = filterBackground;
        this.scaleBrightness = scaleBrightness;
        this.colors = colors;
        this.brightnessDiff = brightnessDiff;
        this.saturationDiff = saturationDiff;
    }

    public BufferedImage getInput() {
        return input;
    }

    public boolean isFilterBackground() {
        return filterBackground;
    }

    public boolean isScaleBrightness() {
        return scaleBrightness;
    }

    public int getColors() {
        return colors;
    }

    public double getBrightnessDiff() {
        return brightnessDiff;
    }

    public double getSaturationDiff() {
        return saturationDiff;
    }
}
