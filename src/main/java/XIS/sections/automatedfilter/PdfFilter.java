package XIS.sections.automatedfilter;

import XIS.sections.Interruptible;
import XIS.toolset.imagetools.HighPassFilterConverter;
import XIS.toolset.io.PdfIO;

import java.io.File;

public class PdfFilter {
    public static void filter(File inputFile, File outputFile, boolean scaleBrightness, double scaleBrightnessVal, int blurPasses, Interruptible interruptible) {
        int size = PdfIO.getNumberOfPages(inputFile);
        var document = PdfIO.createDocument();
        for (int i = 0; i < size; i++) {
            interruptible.reportProgress((1.0*i + 1)/size);
            interruptible.reportProgress("Progress: page " + (i + 1) + "/" + size + ".");
            var image = PdfIO.getPdfAsImage(inputFile, i, interruptible);
            var filteredImage = HighPassFilterConverter.convert(image, blurPasses, scaleBrightness, scaleBrightnessVal);
            PdfIO.addImage(document, filteredImage);
        }
        PdfIO.saveDocumentAndClose(outputFile, document);
        interruptible.reportProgress("Finished");

    }
}
