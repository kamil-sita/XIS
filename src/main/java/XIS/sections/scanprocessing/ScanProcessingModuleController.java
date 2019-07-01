package XIS.sections.scanprocessing;

import XIS.sections.NotifierFactory;
import XIS.sections.XisController;
import XIS.toolset.JavaFXTools;
import XIS.toolset.io.GuiFileIO;
import XIS.toolset.scanfilters.Filter;
import XIS.toolset.scanfilters.FilterArguments;
import XIS.toolset.scanfilters.HighPassFilter;
import XIS.toolset.scanfilters.HighPassFilterArguments;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
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
    private TabPane inputSelector;

    @FXML
    private TabPane methodSelector;

    @FXML
    void loadFilePress(ActionEvent event) {
        var oldInputImage = inputImage;
        var optionalInputImage = GuiFileIO.getImage();
        if (!optionalInputImage.isPresent()) {
            inputImage = oldInputImage;
        } else {
            inputImage = optionalInputImage.get();
            processedImage = inputImage;
            setImagePreview(inputImage);
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

    private static final int METHOD_HIGHPASS = 0;
    private static final int METHOD_QUANTIZATION = 1;

    @FXML
    void runButton(ActionEvent event) {
        if (inputImage == null) {
            getUserFeedback().popup("Can't run without loaded file");
            return;
        }

        Filter filter = null;
        FilterArguments args = null;

        if (methodSelector.getSelectionModel().getSelectedIndex() == METHOD_HIGHPASS) {
            filter = new HighPassFilter();
            var hargs = new HighPassFilterArguments();
            hargs.setScaleBrightness(scaleBrightness.isSelected());
            hargs.setBlackAndWhite(blackAndWhiteCheckbox.isSelected());
            hargs.setScaleBrightnessVal(Math.min(brightnessSlider.getValue()/100.0, 0.995));
            int blurValue = 5;
            try {
                blurValue = Integer.parseInt(blur.getText());
            } catch (Exception e) {
                //failed parsing
            }
            hargs.setBlurPasses(blurValue);
            args = hargs;
        }
        if (methodSelector.getSelectionModel().getSelectedIndex() == METHOD_QUANTIZATION) {
            filter = null;
        }


        FilterCaller.oneImage(inputImage, filter, args, this::setImagePreview, this::setProcessedImage, imagePreview);

    }

    private void setProcessedImage(BufferedImage bufferedImage) {
        processedImage = bufferedImage;
    }

    private void setImagePreview(BufferedImage bufferedImage) {
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
