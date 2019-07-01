package XIS.toolset.scanfilters;

import XIS.sections.Interruptible;
import XIS.sections.scannertonote.ScannerToNote;
import XIS.sections.scannertonote.ScannerToNoteParameters;

import java.awt.image.BufferedImage;

public class QuantizationFilter implements Filter {

    private QuantizationFilterArguments arguments;

    public QuantizationFilter(QuantizationFilterArguments arguments) {
        this.arguments = arguments;
    }

    @Override
    public BufferedImage filter(BufferedImage input, Interruptible interruptible) {
        return ScannerToNote.convert(
                new ScannerToNoteParameters(
                        input,
                        arguments.isFilterBackground(),
                        arguments.isScaleBrightness(),
                        arguments.getColorCount(),
                        arguments.getBrightnessDifference(),
                        arguments.getSaturationDifference()
                ),
                interruptible).orElse(null);
    }
}
