package sections.scannertonote;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sections.Notifier;
import sections.NotifierFactory;
import sections.UserFeedback;
import sections.main.MainViewController;
import toolset.GuiFileIO;
import toolset.imagetools.BufferedImageToFXImage;

import java.awt.image.BufferedImage;

public final class ScannerToNoteController {

    private BufferedImage inputImage;
    private BufferedImage processedImage;

    @FXML
    private TextField colors;

    @FXML
    private ImageView imagePreview;

    @FXML
    private AnchorPane thisAnchorPane;

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
            int colorCount = 5;
            try {
                colorCount = Integer.parseInt(colors.getText());
            } catch (Exception e) {
                //
            }
            processedImage = ScannerToNoteConverter.convert(inputImage, isolateBackground.isSelected(), correctBrightness.isSelected(), colorCount,
                    brightnessDiffSlider.getValue()/100.0,
                    saturationDiffSlider.getValue()/100.0);
            Platform.runLater(() -> setNewImage(processedImage));
        }).start();

    }

    private void setNewImage(BufferedImage bufferedImage) {
        imagePreview.setImage(BufferedImageToFXImage.toFxImage(bufferedImage));
        reAddNotifier();
    }

    private void reAddNotifier() {
        MainViewController.removeNotifier(notifier);
        notifier = NotifierFactory.scalingImageNotifier(processedImage, imagePreview, 130, 0, 1.0);
        MainViewController.addNotifier(notifier);
    }

}
