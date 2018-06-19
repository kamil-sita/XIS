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
        final long length = file.length();
        final int unit = 1024;
        if (length < unit) {
            sizeValue.setText(length + "B");
        } else if (length < unit * unit) {
            sizeValue.setText(length / unit + "kB");
        } else if (length < unit * unit * unit) {
            sizeValue.setText(length / unit / unit + "MB");
        } else {
            sizeValue.setText(length / unit / unit / unit + "GB");
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
