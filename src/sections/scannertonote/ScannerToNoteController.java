package sections.scannertonote;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import sections.*;
import sections.main.MainViewController;
import toolset.imagetools.BufferedImageConvert;
import toolset.io.GuiFileIO;

import java.awt.image.BufferedImage;
import java.util.Optional;

public final class ScannerToNoteController {

    Notifier notifier;
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
        OneBackgroundJobManager.setAndRunJob(new Interruptable() {

            Optional<BufferedImage> optionalImage;

            @Override
            public Runnable getRunnable() {
                return () -> {
                    int colorCount = 5;
                    try {
                        colorCount = Integer.parseInt(colors.getText());
                    } catch (Exception e) {
                        //
                    }

                    optionalImage = ScannerToNoteConverter.convert(
                            plainImage,
                            isolateBackground.isSelected(),
                            correctBrightness.isSelected(),
                            colorCount,
                            brightnessDiffSlider.getValue()/100.0,
                            saturationDiffSlider.getValue()/100.0
                    );
                };
            }

            @Override
            public Runnable onFinish() {
                return () -> {
                    if (optionalImage.isPresent()) {
                        processedImage = optionalImage.get();
                        Platform.runLater(() -> setNewImage());
                    }
                };
            }
        });

    }

    private void setNewImage() {
        var imageToSet = processedImage != null ? processedImage : plainImage;
        imagePreview.setImage(BufferedImageConvert.toFxImage(imageToSet));
        reAddNotifier();
    }

    private void reAddNotifier() {
        MainViewController.removeNotifier(notifier);
        notifier = NotifierFactory.scalingImageNotifier(plainImage, imagePreview, 130, 0, 1.0);
        MainViewController.addNotifier(notifier);
        MainViewController.onWindowSizeChange();
    }

}
