package sections.imageCopyFinder.imageInfoView;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sections.Notifier;
import sections.main.MainViewController;
import universal.tools.imagetools.bufferedimagetools.BufferedImageIO;
import universal.tools.imagetools.bufferedimagetools.BufferedImageToFXImage;

import java.awt.image.BufferedImage;
import java.io.File;

public final class ImageInfoViewController {
    @FXML
    private ImageView imageView;
    private BufferedImage bufferedImage;

    @FXML
    private Label nameValue;

    @FXML
    private Label dimensionsValue;

    @FXML
    private Label sizeValue;

    private AnchorPane iivcAnchorPane;
    private Notifier notifier;

    public void initialize(File file, AnchorPane anchorPane) {
        if (file == null) return;

        iivcAnchorPane = anchorPane;

        nameValue.setText(file.getName());
        double length = file.length();
        final int unit = 1024;
        if (length < unit * unit) {
            sizeValue.setText(String.format("%.3f", length/unit) + "kB");
        } else  {
            sizeValue.setText(String.format("%.3f", length / unit / unit) + "MB");
        }

        notifier = (width, height) -> {
            iivcAnchorPane.setPrefWidth(width/2);
            iivcAnchorPane.setMaxWidth(width/2);

            if (bufferedImage == null) return;

            final int HEIGHT_DIFFERENCE = 460; //const that is "just okay", to calculate height of images.

            double ratio = (bufferedImage.getWidth()*1.0)/(1.0*bufferedImage.getHeight());


            double imgHeight = height - HEIGHT_DIFFERENCE;
            double maxWidth = (width-50)/2.0;

            double imgWidth = imgHeight * ratio;

            if (imgWidth > maxWidth) {
                ratio = maxWidth / imgWidth;
                imgWidth *= ratio;
                imgHeight *= ratio;
            }

            imageView.setFitWidth(imgWidth);
            imageView.setFitHeight(imgHeight);
        };

        MainViewController.addNotifier(notifier);
        MainViewController.reloadView();

        loadImage(file);
    }

    private void loadImage(File resourceFile) {
        new Thread(() -> {
            bufferedImage = BufferedImageIO.getImage(resourceFile);
            if (bufferedImage == null) return;
            Platform.runLater(() -> {
                imageView.setImage(BufferedImageToFXImage.toFxImage(bufferedImage));
                dimensionsValue.setText("(" + bufferedImage.getWidth() + "x" + bufferedImage.getHeight() + ")");
                MainViewController.reloadView();
            });

        }).start();
    }

    public void remove() {
        MainViewController.removeNotifier(notifier);
    }

}
