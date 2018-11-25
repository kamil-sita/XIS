package sections.highpassfilter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import sections.Notifier;
import sections.NotifierFactory;
import sections.UserFeedback;
import sections.main.MainViewController;
import toolset.imagetools.BufferedImageConvert;
import toolset.imagetools.BufferedImageScale;
import toolset.io.GuiFileIO;

import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;

public final class HighPassFilterController {

    private BufferedImage inputImage;
    private BufferedImage processedImage;

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

    Notifier notifier;

    @FXML
    void loadFilePress(ActionEvent event) {
        var oldInputImage = inputImage;
        var optionalInputImage = GuiFileIO.getImage();
        if (!optionalInputImage.isPresent()) {
            inputImage = oldInputImage;
        } else {
            inputImage = optionalInputImage.get();
            processedImage = inputImage;
            setNewImage(inputImage);
        }

    }

    @FXML
    void saveFilePress(ActionEvent event) {
        var imageToSave = processedImage != null ? processedImage : inputImage;
        if (imageToSave == null) {
            UserFeedback.popup("Can't save non-opened image.");
        } else {
            GuiFileIO.saveImage(imageToSave);
        }
    }

    @FXML
    void initialize() {
        reAddNotifier();
    }

    @FXML
    void runButton(ActionEvent event) {
        if (inputImage == null) {
            UserFeedback.popup("Can't run without loaded file");
            return;
        }
        new Thread(() -> {
            int blurValue = 5;
            try {
                blurValue = Integer.parseInt(blur.getText());
            } catch (Exception e) {
                //failed parsing
            }
            UserFeedback.reportProgress("Converting...");
            var output = HighPassFilterConverter.convert(inputImage, blurValue, scaleBrightness.isSelected(), brightnessSlider.getValue()/100.0);
            processedImage = output;
            UserFeedback.reportProgress("Converted image!");
            Platform.runLater(() -> setNewImage(output));

            if (higherQualityPreview.isSelected()) {
                semaphore = new Semaphore(0);
                Platform.runLater(() -> setNewImage(output));
                UserFeedback.reportProgress("Generated preview. Scaling to fit window... Waiting for permit.");
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                UserFeedback.reportProgress("Generated preview. Scaling to fit window...");
                double width = imagePreview.getFitWidth();
                double height = imagePreview.getFitHeight();
                var scaledOutput = BufferedImageScale.getScaledImage(output, width, height);
                Platform.runLater(() -> setNewImage(scaledOutput));
                UserFeedback.reportProgress("Generated scaled preview.");
            } else {
                Platform.runLater(() -> setNewImage(output));
                UserFeedback.reportProgress("Generated preview.");
            }
        }).start();

    }

    Semaphore semaphore;

    private void setNewImage(BufferedImage bufferedImage) {
        imagePreview.setImage(BufferedImageConvert.toFxImage(bufferedImage));
        reAddNotifier();
        MainViewController.forceOnWindowSizeChange();
        if (semaphore != null) semaphore.release();
    }

    private void reAddNotifier() {
        MainViewController.removeNotifier(notifier);
        notifier = NotifierFactory.scalingImageNotifier(processedImage, imagePreview, 90, 10, 1.0);
        MainViewController.addNotifier(notifier);
    }

}
