package toolset.io;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import sections.UserFeedback;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfIO {
    public static List<BufferedImage> getPdfAsImages(File pdfFile) throws IOException {
        System.out.println("start");
        PDDocument document = null;
        PDFRenderer pdfRenderer;
        var listOfImages = new ArrayList<BufferedImage>();
        try {
            document = PDDocument.load(pdfFile);
            pdfRenderer = new PDFRenderer(document);
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                var image = pdfRenderer.renderImageWithDPI(i, 600, ImageType.RGB);
                listOfImages.add(image);
                System.out.println("(" + i + "/" + document.getNumberOfPages() + ")");
            }
        } catch (Exception e) {
            UserFeedback.popup("Error related to pdf");
            return null;
        } finally {
            if (document != null) {
                document.close();
            }
        }
        System.out.println("ex");
        return listOfImages;
    }

    public static PDDocument imagesToDocument(List<BufferedImage> images) {
        PDDocument document = new PDDocument();
        for (var image : images) {
            float width = image.getWidth();
            float height = image.getHeight();
            var page = new PDPage(new PDRectangle(width, height));
            document.addPage(page);

            try {
                var contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false);
                contentStream.drawImage(LosslessFactory.createFromImage(document, image), 0, 0, image.getWidth(), image.getHeight());
                contentStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return document;

    }

}
