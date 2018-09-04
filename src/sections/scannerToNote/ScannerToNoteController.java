package sections.scannertonote;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sections.Notifier;
import sections.NotifierFactory;
import sections.main.MainViewController;
import toolset.GuiFileIO;
import toolset.imagetools.BufferedImageToFXImage;

import java.awt.image.BufferedImage;

public final class ScannerToNoteController {

    private BufferedImage bufferedImage;

    @FXML
    private TextField colors;

    @FXML
    private ImageView imagePreview;

    @FXML
    private AnchorPane thisAnchorPane;

    Notifier notifier;

    @FXML
    void loadFilePress(ActionEvent event) {
        bufferedImage = GuiFileIO.getImage();
        setNewImage();
    }

    @FXML
    void saveFilePress(ActionEvent event) {

    }

    @FXML
    void initialize() {
        reAddNotifier();
    }

    @FXML
    void runButton(ActionEvent event) {
        if (bufferedImage == null) {
            MainViewController.setStatus("Can't run without loaded file");
        }
        bufferedImage = ScannerToNoteConverter.convert(bufferedImage, true, false);
        setNewImage();
    }

    private void setNewImage() {
        imagePreview.setImage(BufferedImageToFXImage.toFxImage(bufferedImage));
        reAddNotifier();
    }

    private void reAddNotifier() {
        MainViewController.removeNotifier(notifier);
        notifier = NotifierFactory.scalingImageNotifier(thisAnchorPane, bufferedImage, imagePreview, 0);
        MainViewController.addNotifier(notifier);
    }

}
