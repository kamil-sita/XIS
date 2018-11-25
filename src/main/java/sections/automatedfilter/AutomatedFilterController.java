package sections.automatedfilter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import sections.NotifierFactory;
import sections.UserFeedback;
import sections.highpassfilter.HighPassFilterConverter;
import sections.main.MainViewController;
import toolset.imagetools.BufferedImageConvert;
import toolset.io.GuiFileIO;
import toolset.io.PdfIO;

import java.awt.image.BufferedImage;
import java.io.File;

public final class AutomatedFilterController {

    private File openPdf = null;

    @FXML
    private TextField blur;

    @FXML
    private ImageView imagePreview;

    @FXML
    private CheckBox scaleBrightness;

    @FXML
    private CheckBox higherQuality;

    @FXML
    private Slider brightnessSlider;

    @FXML
    private Button filterAndSaveButton;

    @FXML
    private Button previewButton;

    @FXML
    private TextField previewPage;

    @FXML
    void filterAndSavePress(ActionEvent event) {
        var optionalSavePdf = GuiFileIO.getSaveDirectory();
        if (!optionalSavePdf.isPresent()) {
            UserFeedback.popup("Wrong save file!");
            return;
        }
        var savePdf = optionalSavePdf.get();
        new Thread(() -> {
            Platform.runLater(() -> UserFeedback.popup("Poup will show up once PDF is filtered"));
            PdfFilter.filter(openPdf, savePdf, higherQuality.isSelected(), scaleBrightness.isSelected(), brightnessSlider.getValue()/100.0);
            Platform.runLater(() -> UserFeedback.popup("Finished filtering pdf"));
        }).start();
    }

    @FXML
    void loadFile(ActionEvent event) {
        lastPage = -1;
        var optionalOpenPdf = GuiFileIO.getPdf();
        if (optionalOpenPdf.isPresent()) {
            openPdf = optionalOpenPdf.get();
            filterAndSaveButton.setDisable(false);
            previewButton.setDisable(false);
        } else {
            filterAndSaveButton.setDisable(true);
            previewButton.setDisable(true);
            UserFeedback.popup("Wrong load file!");
        }
    }

    private int lastPage = -1;
    private BufferedImage lastImage = null;

    @FXML
    void previewPress(ActionEvent event) {
        UserFeedback.reportProgress("Previewing page...");
        int page = PdfIO.getNumberOfPages(openPdf);
        page = (int) (page * 0.1);
        try {
            page = Integer.parseInt(previewPage.getText());
        } catch (Exception e) {
            //
        }
        //variables below are necessary as a workaround for lambda
        final BufferedImage[] inputImage = new BufferedImage[1];
        int finalPage = page;

        new Thread(() -> {
            UserFeedback.reportProgress("Loading image...");
            if (finalPage == lastPage) {
                inputImage[0] = lastImage;
            } else {
                inputImage[0] = PdfIO.getPdfAsImage(openPdf, finalPage);
                lastImage = inputImage[0];
            }
            UserFeedback.reportProgress("Filtering image...");
            var output = HighPassFilterConverter.convert(inputImage[0], 5, scaleBrightness.isSelected(), brightnessSlider.getValue()/100.0, higherQuality.isSelected());
            Platform.runLater(() -> setNewImage(output));
            UserFeedback.reportProgress("Generated preview.");
        }).start();
    }


    private void setNewImage(BufferedImage bufferedImage) {
        imagePreview.setImage(BufferedImageConvert.toFxImage(bufferedImage));
        var notifier = NotifierFactory.scalingImageNotifier(bufferedImage, imagePreview, 90, 10, 1.0);
        MainViewController.addNotifier(notifier);
        MainViewController.forceOnWindowSizeChange();
    }


    @FXML
    void everything(ActionEvent event) {

    }
}
