package XIS.toolset.io;

import XIS.sections.Interruptible;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfIO {
    public static BufferedImage getPdfAsImage(File pdfFile, int i, int dpi, Interruptible interruptible) {
        PDDocument document = null;
        PDFRenderer pdfRenderer;
        BufferedImage bi;
        try {
            document = PDDocument.load(pdfFile);
            pdfRenderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();
            if (i >= numberOfPages) i = numberOfPages - 1;
            bi = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
        } catch (Exception e) {
            interruptible.popup("Error related to pdf");
            return null;
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bi;
    }
}
