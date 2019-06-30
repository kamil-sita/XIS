package XIS.sections.scanprocessing;

import XIS.sections.NotifierFactory;
import XIS.sections.XisController;
import XIS.toolset.JavaFXTools;
import XIS.toolset.imagetools.HighPassFilterConverter;
import XIS.toolset.io.GuiFileIO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public final class ScanProcessingModuleController extends XisController {

    private BufferedImage inputImage;
    private BufferedImage processedImage;

    @FXML
    private TextField blur;

    @FXML
    private ImageView imagePreview;

    @FXML
    private CheckBox scaleBrightness;

    @FXML
    private Slider brightnessSlider;

    @FXML
    private CheckBox blackAndWhiteCheckbox;


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
            getUserFeedback().popup("Can't save non-opened image.");
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
            getUserFeedback().popup("Can't run without loaded file");
            return;
        }
        new Thread(() -> {
            int blurValue = 5;
            try {
                blurValue = Integer.parseInt(blur.getText());
            } catch (Exception e) {
                //failed parsing
            }
            getUserFeedback().reportProgress("Converting...");
            double brightnessFix = brightnessSlider.getValue()/100.0;
            brightnessFix = Math.min(brightnessFix, 0.995);
            var output = HighPassFilterConverter.convert(inputImage, blurValue, scaleBrightness.isSelected(), brightnessFix, blackAndWhiteCheckbox.isSelected());
            processedImage = output;
            getUserFeedback().reportProgress("Converted image!");
            Platform.runLater(() -> setNewImage(output));

            JavaFXTools.showPreview(output, imagePreview, this::setNewImage, getUserFeedback());
        }).start();

    }
    private void setNewImage(BufferedImage bufferedImage) {
        imagePreview.setImage(JavaFXTools.toFxImage(bufferedImage));
        reAddNotifier();
        refreshVista();
    }

    private void reAddNotifier() {
        deregisterAllNotifiers();
        var notifier = NotifierFactory.scalingImageNotifier(processedImage, imagePreview, 10, 262, 1.0);
        registerNotifier(notifier);
    }

}
