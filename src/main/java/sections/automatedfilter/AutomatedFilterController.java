package sections.automatedfilter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import sections.Interruptible;
import sections.NotifierFactory;
import sections.OneBackgroundJobManager;
import sections.XisController;
import toolset.JavaFXTools;
import toolset.imagetools.HighPassFilterConverter;
import toolset.io.GuiFileIO;
import toolset.io.PdfIO;

import java.awt.image.BufferedImage;
import java.io.File;

public final class AutomatedFilterController extends XisController {

    private File openPdf = null;

    @FXML
    private TextField blur;

    @FXML
    private ImageView imagePreview;

    @FXML
    private CheckBox scaleBrightness;

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
        var optionalSavePdf = GuiFileIO.getSaveDirectory("*.pdf");
        if (!optionalSavePdf.isPresent()) {
            getUserFeedback().popup("Wrong save file!");
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

        OneBackgroundJobManager.setAndRunJob(new Interruptible() {
            @Override
            public Runnable getRunnable() {
                return () -> {
                    Platform.runLater(() -> getUserFeedback().popup("Popup will show up once PDF is filtered"));
                    PdfFilter.filter(openPdf, savePdf, scaleBrightness.isSelected(), brightnessSlider.getValue() / 100.0, finalBlurPasses, this);

                };
            }

            @Override
            public Runnable onUninterruptedFinish() {
                return () -> {
                    Platform.runLater(() -> getUserFeedback().popup("Finished filtering pdf"));
                };
            }
        });

        new Thread(() -> {
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
            getUserFeedback().popup("Wrong load file!");
        }
    }

    private int lastPage = -1;
    private BufferedImage lastImage = null;

    @FXML
    void previewPress(ActionEvent event) {
        getUserFeedback().reportProgress("Previewing page...");
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

        final BufferedImage[] output = new BufferedImage[1];

       OneBackgroundJobManager.setAndRunJob(new Interruptible() {
           @Override
           public Runnable getRunnable() {
               return () -> {
                   getUserFeedback().reportProgress("Loading image...");
                   if (finalPage == lastPage) {
                       inputImage[0] = lastImage;
                   } else {
                       inputImage[0] = PdfIO.getPdfAsImage(openPdf, finalPage, this);
                       lastImage = inputImage[0];
                   }
                   getUserFeedback().reportProgress("Filtering image...");
                   output[0] = HighPassFilterConverter.convert(inputImage[0], finalBlurPasses, scaleBrightness.isSelected(), brightnessSlider.getValue() / 100.0);
               };
           }

           @Override
           public Runnable onUninterruptedFinish() {
               return () -> {
                   JavaFXTools.showPreview(output[0], imagePreview, AutomatedFilterController.this::setNewImage, getUserFeedback());
               };
           }
       });

    }


    private void setNewImage(BufferedImage bufferedImage) {
        imagePreview.setImage(JavaFXTools.toFxImage(bufferedImage));
        deregisterAllNotifiers();
        var notifier = NotifierFactory.scalingImageNotifier(bufferedImage, imagePreview, 90, 10, 1.0);
        registerNotifier(notifier);
        refreshVista();
    }
}
