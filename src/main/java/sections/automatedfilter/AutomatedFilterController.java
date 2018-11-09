package sections.automatedfilter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.apache.pdfbox.pdmodel.PDDocument;
import toolset.io.GuiFileIO;

import java.io.File;

public final class AutomatedFilterController {

    private File pdf = null;
    private PDDocument document = null;

    @FXML
    void filterPress(ActionEvent event) {
        new Thread(() -> {
            document = PdfFilter.filter(pdf, false);
            System.out.println("ww");
        }).start();

    }

    @FXML
    void loadFilePress(ActionEvent event) {
        pdf = GuiFileIO.getPdf().get();
    }

    @FXML
    void saveFilePress(ActionEvent event) {
        GuiFileIO.saveDocumentAndClose(document);
    }

}
