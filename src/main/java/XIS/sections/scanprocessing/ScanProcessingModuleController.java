package XIS.sections.scanprocessing;

import XIS.sections.NotifierFactory;
import XIS.sections.XisController;
import XIS.toolset.JavaFXTools;
import XIS.toolset.io.GuiFileIO;
import XIS.toolset.scanfilters.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.File;

public final class ScanProcessingModuleController extends XisController {

    private BufferedImage inputImage;
    private BufferedImage processedImage;

    private File inputFile;
    private File outputFile;

    private static final int METHOD_HIGHPASS = 0;
    private static final int METHOD_QUANTIZATION = 1;

    private static final int SINGLE_IMAGE = 0;
    private static final int MULTIPLE_IMAGES = 1;
    /*
    FXML fields
     */

    //global
    @FXML
    private ImageView imagePreview;

    @FXML
    private TabPane inputSelector;

    @FXML
    private TabPane methodSelector;

    //pdf
    @FXML
    private TextField pageNumberText;

    @FXML
    private TextField dpiText;

    //highpass
    @FXML
    private TextField blurText;

    @FXML
    private Slider brightnessSlider;

    @FXML
    private CheckBox blackAndWhiteCheckbox;

    @FXML
    private CheckBox invertCheckboxHighPass;

    //quantization
    @FXML
    private TextField colorCountTextField;

    @FXML
    private CheckBox correctBrightnessCheckbox;

    @FXML
    private CheckBox isolateBackgroundCheckbox;

    @FXML
    private Slider brightnessDifferenceSlider;

    @FXML
    private Slider saturationDifferenceSlider;

    @FXML
    private CheckBox invertCheckboxQuantization;

    /*
    FXML methods
     */

    @FXML
    void initialize() {
        reAddNotifier();
    }

    @FXML
    void runButton(ActionEvent event) {

        Filter filter = getFilter();

        switch (inputSelector.getSelectionModel().getSelectedIndex()) {
            case SINGLE_IMAGE:
                if (inputImage == null) {
                    getUserFeedback().popup("Can't run without loaded file");
                    return;
                }
                FilterCaller.oneImage(inputImage, filter, this::setSmoothImagePreview, this::setProcessedImage, imagePreview);
                break;
            case MULTIPLE_IMAGES:
                if (inputFile == null || outputFile == null) {
                    getUserFeedback().popup("Can't run without selected files");
                    return;
                }
                int dpi = 300;
                try {
                    dpi = Integer.parseInt(dpiText.getText());
                } catch (Exception e) {

                }
                if (dpi < 10) dpi = 10;
                FilterCaller.multipleImages(inputFile, outputFile, dpi, filter, this::setSmoothImagePreview);
                break;
        }

    }


    //single image

    @FXML
    void loadImagePress(ActionEvent event) {
        var oldInputImage = inputImage;
        var optionalInputImage = GuiFileIO.getImage();
        if (!optionalInputImage.isPresent()) {
            inputImage = oldInputImage;
        } else {
            inputImage = optionalInputImage.get();
            processedImage = inputImage;
            setSmoothImagePreview(inputImage);
        }

    }

    @FXML
    void saveImagePress(ActionEvent event) {
        var imageToSave = processedImage != null ? processedImage : inputImage;
        if (imageToSave == null) {
            getUserFeedback().popup("Can't save non-opened image.");
        } else {
            GuiFileIO.saveImage(imageToSave);
        }
    }

    //pdf file

    @FXML
    void selectInputPdfPress(ActionEvent event) {
        var optionalInputFile = GuiFileIO.getPdf();
        if (optionalInputFile.isPresent()) {
            inputFile = optionalInputFile.get();
        } else {
            getUserFeedback().popup("No PDF selected");
        }
    }

    @FXML
    void selectOutputPdfPress(ActionEvent event) {
        var optionalSavePdf = GuiFileIO.getSaveDirectory("*.pdf");
        if (optionalSavePdf.isPresent()) {
            outputFile = optionalSavePdf.get();
        } else {
            getUserFeedback().popup("Cannot save file there");
        }
    }

    @FXML
    void previewPagePress(ActionEvent event) {
        boolean failure = false;
        int previewPageNumber = 0;
        try {
            previewPageNumber = Integer.parseInt(pageNumberText.getText());
        } catch (Exception e) {
            failure = true;
        }
        if (failure ||  (previewPageNumber < 0)) {
            getUserFeedback().popup("Invalid preview page number");
            return;
        }
        Filter filter = getFilter();
        if (inputFile == null) {
            getUserFeedback().popup("Input file is not selected");
            return;
        }
        int dpi = 300;
        try {
            dpi = Integer.parseInt(dpiText.getText());
        } catch (Exception e) {

        }
        if (dpi < 10) dpi = 10;

        FilterCaller.previewPdf(inputFile, dpi, previewPageNumber, filter, imagePreview, this::setSmoothImagePreview);


    }


    //quantization

    @FXML
    void isolateBackgroundAction(ActionEvent event) {
        brightnessDifferenceSlider.setDisable(!isolateBackgroundCheckbox.isSelected());
        saturationDifferenceSlider.setDisable(!isolateBackgroundCheckbox.isSelected());
    }

    //high pass

    //

    private void setProcessedImage(BufferedImage bufferedImage) {
        processedImage = bufferedImage;
    }

    private void setSmoothImagePreview(BufferedImage bufferedImage) {
        JavaFXTools.showPreview(bufferedImage, imagePreview, this::setBasicImagePreview, getUserFeedback());
    }

    private void setBasicImagePreview(BufferedImage bufferedImage) {
        imagePreview.setImage(JavaFXTools.toFxImage(bufferedImage));
        reAddNotifier();
        refreshVista();
    }

    private void reAddNotifier() {
        deregisterAllNotifiers();
        var notifier = NotifierFactory.scalingImageNotifier(processedImage, imagePreview, 10, 262, 1.0);
        registerNotifier(notifier);
    }


    private Filter getFilter() {
        Arguments arguments = collectArguments();

        switch (methodSelector.getSelectionModel().getSelectedIndex()) {
            case METHOD_HIGHPASS:
                return new HighPassFilter((HighPassFilterArguments) arguments);
            case METHOD_QUANTIZATION:
                return new QuantizationFilter((QuantizationFilterArguments) arguments);
        }
        return null;
    }


    private Arguments collectArguments() {
        switch (methodSelector.getSelectionModel().getSelectedIndex()) {
            case METHOD_HIGHPASS:
                var hargs = new HighPassFilterArguments();
                hargs.setBlackAndWhite(blackAndWhiteCheckbox.isSelected());
                hargs.setScaleBrightnessVal(Math.min(brightnessSlider.getValue()/100.0, 0.995));
                hargs.setInverted(invertCheckboxHighPass.isSelected());
                int blurValue = 5;
                try {
                    blurValue = Integer.parseInt(blurText.getText());
                } catch (Exception e) {
                    //failed parsing
                }
                hargs.setBlurPasses(blurValue);
                return hargs;

            case METHOD_QUANTIZATION:
                var qargs = new QuantizationFilterArguments();
                qargs.setBrightnessDifference(brightnessDifferenceSlider.getValue()/100.0);
                qargs.setSaturationDifference(saturationDifferenceSlider.getValue()/100.0);
                qargs.setInverted(invertCheckboxQuantization.isSelected());
                qargs.setFilterBackground(isolateBackgroundCheckbox.isSelected());
                qargs.setScaleBrightness(correctBrightnessCheckbox.isSelected());

                int colors = 5;
                try {
                    colors = Integer.parseInt(colorCountTextField.getText());
                } catch (Exception e) {
                    //failed parsing
                }
                qargs.setColorCount(colors);
                return qargs;
        }
        return null;
    }
}
