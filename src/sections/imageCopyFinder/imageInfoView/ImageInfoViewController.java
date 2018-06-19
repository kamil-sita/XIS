package sections.imageCopyFinder.imageInfoView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sections.SubUserInterface;
import sections.imageCopyFinder.ComparableImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

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

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    private void loadImageLater() {
        System.out.println("not implemented");
        //todo
    }

    public void setFileInformation(File file) {
        if (file != null) {
            nameValue.setText(file.getName());
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

    private void setDimensionsValue(int width, int height) {
        dimensionsValue.setText(width + " x " + height);
    }

    private void loadDimensions(File resourceFile) {

        //based on https://stackoverflow.com/questions/1559253/java-imageio-getting-image-dimensions-without-reading-the-entire-file

        try (ImageInputStream in = ImageIO.createImageInputStream(resourceFile)) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    setDimensionsValue(reader.getWidth(0), reader.getHeight(0));
                } finally {
                    reader.dispose();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
