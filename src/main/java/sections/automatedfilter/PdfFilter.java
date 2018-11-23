package sections.automatedfilter;

import sections.highpassfilter.HighPassFilterConverter;
import toolset.io.GuiFileIO;
import toolset.io.PdfIO;

import java.io.File;

public class PdfFilter {
    public static void filter(File inputFile, File outputFile, boolean highQuality, boolean scaleBrightness, double scaleBrightnessVal) {
        int size = PdfIO.getNumberOfPages(inputFile);
        var document = PdfIO.createDocument();
        for (int i = 0; i < size; i++) {
            System.out.println("(" + i + "/" + size + ")");
            var image = PdfIO.getPdfAsImage(inputFile, i);
            var filteredImage = HighPassFilterConverter.convert(image, 5, scaleBrightness, scaleBrightnessVal, highQuality);
            PdfIO.addImage(document, filteredImage);
        }
        GuiFileIO.saveDocumentAndCloseNoGui(outputFile, document);
        System.out.println("end");
    }
}
