package toolset.io;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import toolset.imagetools.BufferedImageIO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;

public class GuiFileIO {

    private static File lastFileDirectory = null;

    /**
     * Loading image with native gui
     * @return loaded buffered image
     */
    public static Optional<BufferedImage> getImage() {
        var formats = ImageExtensions.getStarExtensions();

        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open image from ile");
        fileChooser.setInitialDirectory(lastFileDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported images", formats),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) lastFileDirectory = file.getParentFile();

        return BufferedImageIO.getImage(file);
    }

    /**
     * Save image with native gui
     * @param image image to save
     */
    public static void saveImage(BufferedImage image) {
        var formats = ImageExtensions.getStarExtensions();

        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image to file");
        fileChooser.setInitialDirectory(lastFileDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported images", formats),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                ImageIO.write(image, "png", file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lastFileDirectory = file.getParentFile();
        }

    }

    public static Optional<File> getPdf() {

        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open image from ile");
        fileChooser.setInitialDirectory(lastFileDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Pdf", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) lastFileDirectory = file.getParentFile();

        return Optional.ofNullable(file);
    }

    public static void saveDocumentAndClose(PDDocument document) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image to file");
        fileChooser.setInitialDirectory(lastFileDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                document.save(file);
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            lastFileDirectory = file.getParentFile();
        }

    }


}
