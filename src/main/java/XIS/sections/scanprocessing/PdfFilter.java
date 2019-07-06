package XIS.sections.scanprocessing;

import XIS.sections.Interruptible;
import XIS.toolset.JavaFXTools;
import XIS.toolset.scanfilters.Filter;

import java.io.File;

public class PdfFilter {
    public static void filter(File inputFile, File outputFile, Filter filter, Interruptible interruptible, JavaFXTools.SetImageDelegate delegate, int dpi) {
        PdfImageList pdfImageList = null;
        PdfCreator creator = null;
        try {


            pdfImageList = PdfImageList.create(inputFile, dpi);
            if (pdfImageList == null) {
                interruptible.reportProgress("Failure");
                return;
            }
            creator = new PdfCreator();
            creator.setDelegate(delegate);
            for (int i = 0; i < pdfImageList.size(); i++) {
                var image = pdfImageList.at(i);
                var filteredImage = filter.filter(image, interruptible);
                interruptible.reportProgress((1.0*i + 1) / pdfImageList.size());
                interruptible.reportProgress("Progress: page " + (i + 1) + "/" + pdfImageList.size() + ".");
                creator.append(filteredImage);

            }
            interruptible.reportProgress("Finished");
            creator.save(outputFile);


        } finally {
            try {
                if (pdfImageList != null) {
                    pdfImageList.close();
                }
                if (creator != null) {
                    creator.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
