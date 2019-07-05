package XIS.sections.automatedfilter;

import XIS.toolset.JavaFXTools;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfCreator implements AutoCloseable {

    private PDDocument document = new PDDocument();
    private JavaFXTools.SetImageDelegate delegate = null;

    public void setDelegate(JavaFXTools.SetImageDelegate delegate) {
        this.delegate = delegate;
    }

    public void append(BufferedImage image) {
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
        document.addPage(new PDPage(new PDRectangle()));

        if (delegate != null) {
            delegate.set(image);
        }
    }

    public void save(File file) {
        try {
            document.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {

    }
}
