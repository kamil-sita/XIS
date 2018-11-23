package sections.automatedfilter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import toolset.io.GuiFileIO;

import java.io.File;

public final class AutomatedFilterController {

    private File openPdf = null;

    @FXML
    private CheckBox scaleBrightness;

    @FXML
    private CheckBox higherQuality;

    @FXML
    private Slider brightnessSlider;

    @FXML
    void filterPress(ActionEvent event) {


    }

    @FXML
    void everything(ActionEvent event) {
        var optionalOpenPdf = GuiFileIO.getPdf();
        var optionalSavePdf = GuiFileIO.getSaveDirectory();
        if (optionalOpenPdf.isPresent() && optionalSavePdf.isPresent()) {
            openPdf = optionalOpenPdf.get();
        } else {
            return;
        }
        var savePdf = optionalSavePdf.get();
        new Thread(() -> {
            PdfFilter.filter(openPdf, savePdf, higherQuality.isSelected(), scaleBrightness.isSelected(), brightnessSlider.getValue()/100.0);
        }).start();
    }

    @FXML
    void saveFilePress(ActionEvent event) {

    }

}
