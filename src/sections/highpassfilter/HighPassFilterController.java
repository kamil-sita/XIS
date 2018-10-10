package sections.highpassfilter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import sections.Notifier;
import sections.NotifierFactory;
import sections.UserFeedback;
import sections.main.MainViewController;
import toolset.GuiFileIO;
import toolset.imagetools.BufferedImageToFXImage;

import java.awt.image.BufferedImage;

public final class HighPassFilterController {

    private BufferedImage inputImage;
    private BufferedImage processedImage;

    @FXML
    private TextField blur;

    @FXML
    private ImageView imagePreview;

    @FXML
    private CheckBox scaleBrightness;

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
            var output = HighPassFilterConverter.convert(inputImage, blurValue, scaleBrightness.isSelected()).getBufferedImage();
            processedImage = output;
            Platform.runLater(() -> setNewImage(output));
        }).start();

    }

    private void setNewImage(BufferedImage bufferedImage) {
        imagePreview.setImage(BufferedImageToFXImage.toFxImage(bufferedImage));
        reAddNotifier();
        MainViewController.onWindowSizeChange();
    }

    private void reAddNotifier() {
        MainViewController.removeNotifier(notifier);
        notifier = NotifierFactory.scalingImageNotifier(processedImage, imagePreview, 90, 10, 1.0);
        MainViewController.addNotifier(notifier);
    }

}
