package toolset;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import toolset.imagetools.BufferedImageIO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;

public class GuiFileIO {

    private static String[] extensionList;
    private static File lastFileDirectory = null;

    /**
     * Loading image with native gui
     * @return loaded buffered image
     */
    public static Optional<BufferedImage> getImage() {
        lazyGenerateFormats();

        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open image from ile");
        fileChooser.setInitialDirectory(lastFileDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported images", extensionList),
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
        lazyGenerateFormats();

        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image to file");
        fileChooser.setInitialDirectory(lastFileDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported images", extensionList),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                ImageIO.write(image, "png", file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (file != null) lastFileDirectory = file.getParentFile();

    }

    private static void lazyGenerateFormats() {
        if (extensionList != null) return;

        extensionList = ImageIO.getReaderFormatNames();

        for (int i = 0; i < extensionList.length; i++) {
            extensionList[i] = "*." + extensionList[i];
        }

    }
}
