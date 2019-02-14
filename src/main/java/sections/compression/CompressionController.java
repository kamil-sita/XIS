package sections.compression;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import sections.*;
import sections.main.MainViewController;
import toolset.JavaFXTools;
import toolset.io.BinaryIO;
import toolset.io.GuiFileIO;

import java.awt.image.BufferedImage;

public final class CompressionController {

    Notifier notifier;
    private BufferedImage loadedImage = null;
    private BufferedImage processedImage = null;
    private CompressedAndPreview compressedAndPreview = null;

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
    void imageClick(MouseEvent event) {
        if (processedImage != null) {
            UserFeedback.openInDefault(processedImage);
        } else if (loadedImage != null) {
            UserFeedback.openInDefault(loadedImage);
        }
    }

    @FXML
    void loadFilePress(ActionEvent event) {
        //opens image. If image == null, uses old image instead
        var optionalInputImage = GuiFileIO.getImage();
        if (optionalInputImage.isPresent()) {
            loadedImage = optionalInputImage.get();
            processedImage = null;
            JavaFXTools.showPreview(loadedImage, true, imagePreview, this::setNewImage);
            imagePreview.getStyleClass().add("clickable");
        }
    }

    @FXML
    void loadCompressedFilePress(ActionEvent event) {
        var file = BinaryIO.getBitSequenceFromUserSelected();
        Header h = new Header(new BitSequenceDecoder(file.get()));
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
        if (compressedAndPreview == null) {
            UserFeedback.popup("Can't save non-processed image.");
        } else {
            BinaryIO.writeBitSequenceToUserSelected(compressedAndPreview.getBitSequence());
        }
    }

    @FXML
    void initialize() {
        reAddNotifier();
    }

    @FXML
    void runButton(ActionEvent event) {
        if (loadedImage == null) {
            UserFeedback.popup("Can't run without loaded file");
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
            UserFeedback.reportProgress("Some of values are not integer values.");
        }

        if (yWeightValue < 0 || cWeightValue < 0 || blockSizeValue < 2) {
            UserFeedback.reportProgress("Some of values are smaller than their minimum size");
        }

        int finalYWeightValue = yWeightValue;
        int finalCWeightValue = cWeightValue;
        int finalBlockSizeValue = blockSizeValue;
        OneBackgroundJobManager.setAndRunJob(new Interruptible() {

            @Override
            public Runnable getRunnable() {
                return () -> compressedAndPreview = Compression.compress(finalYWeightValue, finalCWeightValue, finalBlockSizeValue, this, loadedImage).get();
            };

            @Override
            public Runnable onUninterruptedFinish() {
                return () -> {
                    processedImage = compressedAndPreview.getBufferedImage();
                    int size = compressedAndPreview.getBitSequence().getSize()/8/1024;
                    Platform.runLater(() -> {
                        outputSize.setText("Output size: " + size + "kB");
                    });
                    setNewImage(processedImage);
                };
            }
        });

    }

    private void setNewImage(BufferedImage bufferedImage) {
        imagePreview.setImage(JavaFXTools.toFxImage(bufferedImage));
        reAddNotifier();
    }

    private void reAddNotifier() {
        MainViewController.removeNotifier(notifier);
        notifier = NotifierFactory.scalingImageNotifier(loadedImage, imagePreview, 130, 0, 1.0);
        MainViewController.addNotifier(notifier);
        MainViewController.refreshVista();
    }

}
