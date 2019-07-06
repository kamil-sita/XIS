package XIS.sections.scanprocessing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class PdfImageList implements Iterable<BufferedImage>, AutoCloseable {

   // static MemoryUsageSetting memoryUsageSetting = new MemoryUsageSetting();
    private PDDocument document;
    private PDFRenderer renderer;
    private long size;
    private int dpi = 300;


    public static PdfImageList create(File file, int dpi) {
        try {
            var document = PDDocument.load(file);
            int size = document.getNumberOfPages();
            return new PdfImageList(document, size, dpi);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PdfImageList(PDDocument document, long size, int dpi) {
        this.document = document;
        this.size = size;
        this.dpi = dpi;
        renderer = new PDFRenderer(document);
    }

    public long size() {
        return size;
    }

    public BufferedImage at(int id) {
        try {
            return renderer.renderImageWithDPI(id, dpi, ImageType.RGB);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        document.close();
    }


    @Override
    public Iterator<BufferedImage> iterator() {
        return new Iterator<BufferedImage>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public BufferedImage next() {
                index++;
                return at(index - 1);
            }
        };
    }

}
