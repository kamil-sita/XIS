package sections.compression;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import sections.*;
import sections.main.MainViewController;
import toolset.JavaFXTools;
import toolset.io.GuiFileIO;

import java.awt.image.BufferedImage;

final class CompressionController {

    Notifier notifier;
    private BufferedImage loadedImage = null;
    private BufferedImage processedImage = null;
    @FXML
    private ImageView imagePreview;


    @FXML
    private TextField yWeight;

    @FXML
    private TextField cWeight;


    @FXML
    void loadFilePress(ActionEvent event) {
        //opens image. If image == null, uses old image instead
        var optionalInputImage = GuiFileIO.getImage();
        if (optionalInputImage.isPresent()) {
            loadedImage = optionalInputImage.get();
            processedImage = null;
            JavaFXTools.showPreview(loadedImage, true, imagePreview, this::setNewImage);
        }
    }

    @FXML
    void highQualityImagesPress(ActionEvent event) {
        yWeight.setText("128");
        cWeight.setText("64");
    }


    @FXML
    void mediumQualityPhotosPress(ActionEvent event) {
        yWeight.setText("16");
        cWeight.setText("8");
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
        if (loadedImage == null) {
            UserFeedback.popup("Can't run without loaded file");
            return;
        }
        OneBackgroundJobManager.setAndRunJob(new Interruptible() {
            @Override
            public Runnable getRunnable() {
                return null;
            }

            @Override
            public Runnable onUninterruptedFinish() {
                return null;
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
