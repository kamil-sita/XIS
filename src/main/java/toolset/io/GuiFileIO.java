package toolset.io;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
        Stage stage = new Stage();

        File file = getImageFileChooser(FileChooserType.open).showOpenDialog(stage);

        if (file != null) lastFileDirectory = file.getParentFile();

        return BufferedImageIO.getImage(file);
    }

    /**
     * Save image with native gui
     * @param image image to save
     */
    public static void saveImage(BufferedImage image) {
        Stage stage = new Stage();

        File file = getImageFileChooser(FileChooserType.save).showSaveDialog(stage);

        if (file != null) {
            try {
                ImageIO.write(image, "png", file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lastFileDirectory = file.getParentFile();
        }

    }

    private enum FileChooserType {
        open, save
    }

    private static FileChooser getImageFileChooser(FileChooserType fileChooserType) {
        var formats = ImageExtensions.getStarExtensions();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(fileChooserType == FileChooserType.open ? "Open image from file" : "Save image to file");
        fileChooser.setInitialDirectory(lastFileDirectory);
        if (fileChooserType == FileChooserType.open) {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Supported images", formats),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
        } else {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG image", "*.png", "*.PNG")
            );
        }

        return fileChooser;

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


    public static Optional<File> getSaveDirectory() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image to file");
        fileChooser.setInitialDirectory(lastFileDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return Optional.ofNullable(fileChooser.showSaveDialog(stage));

    }


}
