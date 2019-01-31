package sections.automatedfilter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import sections.Notifier;
import sections.NotifierFactory;
import sections.UserFeedback;
import sections.main.MainViewController;
import toolset.JavaFXTools;
import toolset.imagetools.HighPassFilterConverter;
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
    private CheckBox higherQualityPreview;

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
        int blurPasses = 5;
        try {
            blurPasses = Integer.parseInt(blur.getText());
        } catch (Exception e) {
            //
        }
        int finalBlurPasses = blurPasses;

        new Thread(() -> {
            Platform.runLater(() -> UserFeedback.popup("Popup will show up once PDF is filtered"));
            PdfFilter.filter(openPdf, savePdf, scaleBrightness.isSelected(), brightnessSlider.getValue()/100.0, finalBlurPasses);
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

        int blurPasses = 5;
        try {
            blurPasses = Integer.parseInt(blur.getText());
        } catch (Exception e) {
            //
        }

        //variables below are necessary as a workaround for lambda
        final BufferedImage[] inputImage = new BufferedImage[1];
        int finalPage = page;
        int finalBlurPasses = blurPasses;

        new Thread(() -> {
            UserFeedback.reportProgress("Loading image...");
            if (finalPage == lastPage) {
                inputImage[0] = lastImage;
            } else {
                inputImage[0] = PdfIO.getPdfAsImage(openPdf, finalPage);
                lastImage = inputImage[0];
            }
            UserFeedback.reportProgress("Filtering image...");
            var output = HighPassFilterConverter.convert(inputImage[0], finalBlurPasses, scaleBrightness.isSelected(), brightnessSlider.getValue()/100.0);

            JavaFXTools.showPreview(output, higherQualityPreview.isSelected(), imagePreview, this::setNewImage);

        }).start();
    }


    private Notifier oldNotifier;

    private void setNewImage(BufferedImage bufferedImage) {

        imagePreview.setImage(JavaFXTools.toFxImage(bufferedImage));
        var notifier = NotifierFactory.scalingImageNotifier(bufferedImage, imagePreview, 90, 10, 1.0);
        MainViewController.removeNotifier(oldNotifier);
        MainViewController.addNotifier(notifier);
        MainViewController.refreshVista();
        oldNotifier = notifier;
    }
}
