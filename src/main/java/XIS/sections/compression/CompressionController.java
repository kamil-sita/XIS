package XIS.sections.compression;

import XIS.sections.*;
import XIS.toolset.JavaFXTools;
import XIS.toolset.io.BinaryIO;
import XIS.toolset.io.GuiFileIO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.util.Optional;

public final class CompressionController extends XisController {

    Notifier notifier;
    private BufferedImage loadedImage = null;
    private BufferedImage processedImage = null;
    private CompressionOutput compressionOutput = null;
    private BitSequence compressedImage = null;

    @FXML
    private ImageView imagePreview;

    @FXML
    private TextField yWeight;

    @FXML
    private TextField cWeight;

    @FXML
    private TextField blockSize;

    @FXML
    private Label outputSize;

    @FXML
    private CheckBox allowReordering;

    @FXML
    void loadFilePress(ActionEvent event) {
        //opens image. If image == null, uses old image instead
        var optionalInputImage = GuiFileIO.getImage();
        if (optionalInputImage.isPresent()) {
            loadedImage = optionalInputImage.get();
            JavaFXTools.showPreview(loadedImage, imagePreview, this::setNewImage, getUserFeedback());
            processedImage = null;
            compressedImage = null;
        }
    }

    @FXML
    void loadCompressedFilePress(ActionEvent event) {
        var file = BinaryIO.getBitSequenceFromUserSelected();
        if (file.isPresent()) {
            compressedImage = file.get();
            processedImage = null;
            loadedImage = null;
            decompress();
        }
    }

    @FXML
    void highQualityImagesPress(ActionEvent event) {
        yWeight.setText("128");
        cWeight.setText("64");
        blockSize.setText("8");
    }


    @FXML
    void mediumQualityPhotosPress(ActionEvent event) {
        yWeight.setText("16");
        cWeight.setText("8");
        blockSize.setText("16");
    }

    @FXML
    void saveFilePress(ActionEvent event) {
        if (compressionOutput == null) {
            if (loadedImage == null) {
                getUserFeedback().popup("No image loaded!");
            } else {
                GuiFileIO.saveImage(loadedImage);
            }
        } else {
            BinaryIO.writeBitSequenceToUserSelected(compressionOutput.getBitSequence());
        }
    }

    @FXML
    void initialize() {
        reAddNotifier();
    }

    void decompress() {
        OneBackgroundJobManager.setAndRunJob(new Interruptible() {
            private Optional<BufferedImage> image;
            @Override
            public Runnable getRunnable() {
                return () -> {
                    image = LosticCompression.decompress(compressedImage, this);
                };
            }

            @Override
            public Runnable onUninterruptedFinish() {
                return () -> {
                    if (image.isPresent()) {
                        loadedImage = image.get();
                        JavaFXTools.showPreview(loadedImage, imagePreview, bufferedImage -> setNewImage(loadedImage), getUserFeedback());
                    }
                };
            }
        });
    }

    @FXML
    void statisticPress() {
        getUserFeedback().longPopupTextArea(compressionOutput.getStatistic().toString());
    }

    @FXML
    void compressButton(ActionEvent event) {
        if (loadedImage == null) {
            getUserFeedback().popup("Can't run without loaded file");
            return;
        }

        int yWeightValue = 0;
        int cWeightValue = 0;
        int blockSizeValue = 0;
        try {
            yWeightValue = Integer.parseInt(yWeight.getText());
            cWeightValue = Integer.parseInt(cWeight.getText());
            blockSizeValue = Integer.parseInt(blockSize.getText());
        } catch (Exception e) {
            getUserFeedback().reportProgress("Some of values are not integer values.");
            return;
        }

        if (yWeightValue < 0 || cWeightValue < 0 || blockSizeValue < 2) {
            getUserFeedback().reportProgress("Some of values are smaller than their minimum size");
            return;
        }

        CompressionArguments compressionArguments = new CompressionArguments();
        compressionArguments.setyWeight(yWeightValue);
        compressionArguments.setcWeight(cWeightValue);
        compressionArguments.setBlockSize(blockSizeValue);
        compressionArguments.setAllowReordering(allowReordering.isSelected());
        compressionArguments.setInput(loadedImage);

        OneBackgroundJobManager.setAndRunJob(new Interruptible() {
            @Override
            public Runnable getRunnable() {
                return () -> {
                    compressionOutput = LosticCompression.compress(compressionArguments, this).orElse(null);
                };
            };

            @Override
            public Runnable onUninterruptedFinish() {
                return () -> {
                    processedImage = compressionOutput.getPreviewImage();
                    int size = compressionOutput.getBitSequence().getSize()/8/1024;
                    Platform.runLater(() -> {
                        outputSize.setText("Output size: " + size + "kB");
                        JavaFXTools.showPreview(processedImage, imagePreview, CompressionController.this::setNewImage, getUserFeedback());
                    });
                };
            }
        });

    }

    private void setNewImage(BufferedImage bufferedImage) {
        imagePreview.setImage(JavaFXTools.toFxImage(bufferedImage));
        reAddNotifier();
    }

    private void reAddNotifier() {
        deregisterNotifier(notifier);
        notifier = NotifierFactory.scalingImageNotifier(loadedImage, imagePreview, 130, 0, 1.0);
        registerNotifier(notifier);
        refreshVista();
    }

}
