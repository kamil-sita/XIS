package toolset;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import toolset.imagetools.BufferedImageIO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class GuiFileIO {

    private static String[] extensionList;

    //getting image with native gui
    public static BufferedImage getImage() {
        generateFormats();

        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported images", extensionList),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);

        return BufferedImageIO.getImage(file);
    }

    //saving image with native gui
    public static void saveImage(BufferedImage image) {
        generateFormats();

        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image");
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

    }

    private static void generateFormats() {
        if (extensionList != null) return;

        extensionList = ImageIO.getReaderFormatNames();

        for (int i = 0; i < extensionList.length; i++) {
            extensionList[i] = "*." + extensionList[i];
        }

    }
}
