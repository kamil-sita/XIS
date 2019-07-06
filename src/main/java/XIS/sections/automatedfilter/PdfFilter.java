package XIS.sections.automatedfilter;

import XIS.sections.Interruptible;
import XIS.toolset.JavaFXTools;
import XIS.toolset.scanfilters.Filter;

import java.io.File;

public class PdfFilter {
    public static void filter(File inputFile, File outputFile, Filter filter, Interruptible interruptible, JavaFXTools.SetImageDelegate delegate) {
        var pdfImageList = PdfImageList.create(inputFile, 300);
        if (pdfImageList == null) {
            interruptible.reportProgress("Failure");
            return;
        }
        var creator = new PdfCreator();
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
        try {
            pdfImageList.close();
            creator.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
