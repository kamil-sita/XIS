package XIS.sections.scannertonote;

import XIS.sections.*;
import XIS.toolset.JavaFXTools;
import XIS.toolset.io.GuiFileIO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.util.Optional;

public final class ScannerToNoteModuleController extends XisController {

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
            JavaFXTools.showPreview(plainImage, imagePreview, this::setNewImage, getUserFeedback());
        }
    }

    @FXML
    void saveFilePress(ActionEvent event) {
        if (processedImage == null) {
            getUserFeedback().popup("Can't save non-processed image.");
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
            getUserFeedback().popup("Can't run without loaded file");
            return;
        }
        SingleJobManager.setAndRunJob(new Interruptible() {

            BufferedImage image = null;

            @Override
            public Runnable getRunnable() {
                return () -> {
                    int colorCount = 5;
                    try {
                        colorCount = Integer.parseInt(colors.getText());
                    } catch (Exception e) {
                        //
                    }

                    Optional<BufferedImage> optionalImage = ScannerToNote.convert(
                            new ScannerToNote.ScannerToNoteParameters(
                                    plainImage,
                                    isolateBackground.isSelected(),
                                    correctBrightness.isSelected(),
                                    colorCount,
                                    brightnessDiffSlider.getValue()/100.0,
                                    saturationDiffSlider.getValue()/100.0
                            ),
                            this
                    );

                    image = optionalImage.orElse(null);
                };
            }

            @Override
            public Runnable onUninterruptedFinish() {
                return () -> {
                    if (image != null) {
                        processedImage = image;
                        JavaFXTools.showPreview(processedImage, imagePreview, (BufferedImage bu) -> setNewImage(bu), getUserFeedback());
                    }
                };
            }
        });

    }

    private void setNewImage(BufferedImage bufferedImage) {
        imagePreview.setImage(JavaFXTools.toFxImage(bufferedImage));
        reAddNotifier();
    }

    private void reAddNotifier() {
        deregisterAllNotifiers();
        notifier = NotifierFactory.scalingImageNotifier(plainImage, imagePreview, 130, 0, 1.0);
        registerNotifier(notifier);
        refreshVista();
    }

}
