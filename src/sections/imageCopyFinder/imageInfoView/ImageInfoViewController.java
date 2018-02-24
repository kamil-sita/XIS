package sections.imageCopyFinder.imageInfoView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

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

    public void setInformation (File file, int width, int height) {
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
