package sections.automatedfilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import sections.highpassfilter.HighPassFilterConverter;
import toolset.io.PdfIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfFilter {
    public static PDDocument filter(File inputFile, boolean highQuality) {
        List<BufferedImage> images = null;
        try {
            images = PdfIO.getPdfAsImages(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < images.size(); i++) {
            System.out.println("(" + i + "/" + images.size() + ")");
            images.set(i, HighPassFilterConverter.convert(images.get(i), 5, true, 0.90, highQuality));
        }

        return PdfIO.imagesToDocument(images);
    }
}
