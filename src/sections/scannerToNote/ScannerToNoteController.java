package sections.scannertonote;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
    void loadFilePress(ActionEvent event) {
        bufferedImage = GuiFileIO.getImage();
        imagePreview.setImage(BufferedImageToFXImage.toFxImage(bufferedImage));
    }

    @FXML
    void saveFilePress(ActionEvent event) {

    }

}
