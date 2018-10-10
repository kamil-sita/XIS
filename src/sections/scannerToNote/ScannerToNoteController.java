package sections.scannertonote;

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
import toolset.GuiFileIO;
import toolset.imagetools.BufferedImageToFXImage;

import java.awt.image.BufferedImage;

public final class ScannerToNoteController {

    private BufferedImage plainImage = null;
    private BufferedImage processedImage = null;

    @FXML
    private TextField colors;

    @FXML
    private ImageView imagePreview;

    @FXML
    private CheckBox isolateBackground;

    @FXML
    private CheckBox correctBrightness;

    @FXML
    private Slider brightnessDiffSlider;

    @FXML
    private Slider saturationDiffSlider;


    Notifier notifier;

    @FXML
    void isolateBackgroundAction(ActionEvent event) {
        brightnessDiffSlider.setDisable(!isolateBackground.isSelected());
        saturationDiffSlider.setDisable(!isolateBackground.isSelected());
    }

    @FXML
    void loadFilePress(ActionEvent event) {
        //opens image. If image == null, uses old image instead
        var optionalInputImage = GuiFileIO.getImage();
        if (optionalInputImage.isPresent()) {
            plainImage = optionalInputImage.get();
            processedImage = null;
            setNewImage();
        }
    }

    @FXML
    void saveFilePress(ActionEvent event) {
        if (processedImage == null) {
            UserFeedback.popup("Can't save non-processed image.");
        } else {
            GuiFileIO.saveImage(processedImage);
        }
    }

    @FXML
    void initialize() {
        reAddNotifier();
    }

    @FXML
    void runButton(ActionEvent event) {
        if (plainImage == null) {
            UserFeedback.popup("Can't run without loaded file");
            return;
        }
        new Thread(() -> {
            int colorCount = 5;
            try {
                colorCount = Integer.parseInt(colors.getText());
            } catch (Exception e) {
                //
            }
            processedImage = ScannerToNoteConverter.convert(plainImage, isolateBackground.isSelected(), correctBrightness.isSelected(), colorCount,
                    brightnessDiffSlider.getValue()/100.0,
                    saturationDiffSlider.getValue()/100.0);
            Platform.runLater(this::setNewImage);
        }).start();

    }

    private void setNewImage() {
        var imageToSet = processedImage != null ? processedImage : plainImage;
        imagePreview.setImage(BufferedImageToFXImage.toFxImage(imageToSet));
        reAddNotifier();
    }

    private void reAddNotifier() {
        MainViewController.removeNotifier(notifier);
        notifier = NotifierFactory.scalingImageNotifier(plainImage, imagePreview, 130, 0, 1.0);
        MainViewController.addNotifier(notifier);
        MainViewController.onWindowSizeChange();
    }

}
