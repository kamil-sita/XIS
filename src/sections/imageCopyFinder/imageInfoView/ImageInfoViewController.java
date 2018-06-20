package sections.imageCopyFinder.imageInfoView;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import universal.tools.imagetools.bufferedimagetools.BufferedImageIO;
import universal.tools.imagetools.bufferedimagetools.BufferedImageToFXImage;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageInfoViewController {
    @FXML
    private ImageView imageView;

    @FXML
    private Label nameValue;

    @FXML
    private Label dimensionsValue;

    @FXML
    private Label sizeValue;

    public void setFileInformation(File file) {
        if (file == null) return;

        nameValue.setText(file.getName());
        double length = file.length();
        final int unit = 1024;
        if (length < unit * unit) {
            sizeValue.setText(String.format("%.3f", length/unit) + "kB");
        } else  {
            sizeValue.setText(String.format("%.3f", length / unit / unit) + "MB");
        }
        loadImage(file);
    }

    private void loadImage(File resourceFile) {
        new Thread(() -> {
            BufferedImage bi = BufferedImageIO.getImage(resourceFile);
            if (bi == null) return;
            Platform.runLater(() -> {
                imageView.setImage(BufferedImageToFXImage.toFxImage(bi));
                dimensionsValue.setText("(" + bi.getWidth() + "x" + bi.getHeight() + ")");
            });

        }).start();
    }

}
