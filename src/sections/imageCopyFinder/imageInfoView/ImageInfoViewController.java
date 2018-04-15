package sections.imageCopyFinder.imageInfoView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sections.ModuleTemplate;
import sections.imageCopyFinder.ComparableImage;
import sections.imageCopyFinder.ImageComparator;
import universal.tools.BufferedImageTools.BufferedImageIO;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;

public class ImageInfoViewController {

    private static final String LOCATION_VIEW = "sections/imageCopyFinder/imageInfoView/imageInfoView.fxml";

    @FXML
    private ImageView imageView;

    @FXML
    private Label nameValue;

    @FXML
    private Label dimensionsValue;

    @FXML
    private Label sizeValue;

    private ComparableImage comparableImage;
    private AnchorPane anchorPane;
    private BufferedImage preview;

    public ImageInfoViewController(ComparableImage comparableImage, int width, int height) {
        this.comparableImage = comparableImage;
        URL url = ModuleTemplate.class.getClassLoader().getResource(LOCATION_VIEW);
        try {
            anchorPane = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        preview = BufferedImageIO.getImage(comparableImage.getFile());
        setInformation(comparableImage.getFile(), preview.getWidth(), preview.getHeight());
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    private void setInformation (File file, int width, int height) {
        if (file != null) {
            nameValue.setText(file.getName());
            dimensionsValue.setText(width + " x " + height);

            final long length = file.length();
            final int unit = 1024;
            if (length < unit) {
                sizeValue.setText(length + "B");
            } else if (length < unit * unit) {
                sizeValue.setText(length/unit + "kB");
            } else if (length < unit * unit * unit) {
                sizeValue.setText(length/unit/unit + "MB");
            } else {
                sizeValue.setText(length/unit/unit/unit + "GB");
            }

        }
    }

}
