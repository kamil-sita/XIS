package XIS.sections.scanprocessing.quantization;

import java.awt.image.BufferedImage;

public class ScannerToNoteParameters {
    private final BufferedImage input;
    private final boolean filterBackground;
    private final boolean scaleBrightness;
    private final int colors;
    private final double brightnessDiff;
    private final double saturationDiff;
    private final boolean inverted;

    public ScannerToNoteParameters(BufferedImage input, boolean filterBackground, boolean scaleBrightness, int colors, double brightnessDiff, double saturationDiff, boolean inverted) {
        this.input = input;
        this.filterBackground = filterBackground;
        this.scaleBrightness = scaleBrightness;
        this.colors = colors;
        this.brightnessDiff = brightnessDiff;
        this.saturationDiff = saturationDiff;
        this.inverted = inverted;
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

    public boolean isInverted() {
        return inverted;
    }
}
