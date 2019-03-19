package XIS.toolset.io;

import XIS.sections.Interruptible;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfIO {
    public static BufferedImage getPdfAsImage(File pdfFile, int i, Interruptible interruptible) {
        PDDocument document = null;
        PDFRenderer pdfRenderer;
        BufferedImage bi;
        try {
            document = PDDocument.load(pdfFile);
            pdfRenderer = new PDFRenderer(document);
            bi = pdfRenderer.renderImageWithDPI(i, 300, ImageType.RGB);
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

    public static int getNumberOfPages(File pdfFile) {
        PDDocument document = null;
        int number = -1;
        try {
            document = PDDocument.load(pdfFile);
            number = document.getNumberOfPages();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (document != null) document.close();
            } catch (Exception e) {
                //
            }

        }
        return number;
    }

    public static PDDocument createDocument() {
        return new PDDocument();
    }

    public static void addImage(PDDocument document, BufferedImage image) {
        float width = image.getWidth();
        float height = image.getHeight();
        var page = new PDPage(new PDRectangle(width, height));
        document.addPage(page);

        try {
            var contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false);
            contentStream.drawImage(LosslessFactory.createFromImage(document, image), 0, 0, image.getWidth(), image.getHeight());
            contentStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDocumentAndClose(File file, PDDocument document) {
        if (file != null) {
            try {
                document.save(file);
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
