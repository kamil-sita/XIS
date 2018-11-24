package sections.automatedfilter;

import sections.UserFeedback;
import sections.highpassfilter.HighPassFilterConverter;
import toolset.io.PdfIO;

import java.io.File;

public class PdfFilter {
    public static void filter(File inputFile, File outputFile, boolean highQuality, boolean scaleBrightness, double scaleBrightnessVal) {
        int size = PdfIO.getNumberOfPages(inputFile);
        var document = PdfIO.createDocument();
        for (int i = 0; i < size; i++) {
            UserFeedback.reportProgress((1.0*i)/size);
            UserFeedback.reportProgress("Progress: page " + (i + 1) + "/" + size + ".");
            var image = PdfIO.getPdfAsImage(inputFile, i);
            var filteredImage = HighPassFilterConverter.convert(image, 5, scaleBrightness, scaleBrightnessVal, highQuality);
            PdfIO.addImage(document, filteredImage);
        }
        PdfIO.saveDocumentAndClose(outputFile, document);
        UserFeedback.reportProgress("Finished");

    }
}