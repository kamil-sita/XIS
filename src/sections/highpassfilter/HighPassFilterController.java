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
    private CheckBox scaleContrast;

    Notifier notifier;

    @FXML
    void loadFilePress(ActionEvent event) {
        //opens image. If image == null, uses old image instead
        var oldInputImage = inputImage;
        inputImage = GuiFileIO.getImage();
        if (inputImage == null) inputImage = oldInputImage;
        processedImage = inputImage;

        setNewImage(inputImage);
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
            int blurValue = 1;
            try {
                blurValue = Integer.parseInt(blur.getText());
            } catch (Exception e) {
                //
            }
            var output = HighPassFilterConverter.convert(inputImage, blurValue, scaleContrast.isSelected()).getBufferedImage();
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
        notifier = NotifierFactory.scalingImageNotifier(processedImage, imagePreview, 130, 0, 1.0);
        MainViewController.addNotifier(notifier);
    }

}
