package sections.imageCopyFinder.imageInfoView;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sections.Notifier;
import sections.main.MainViewController;
import toolset.imagetools.BufferedImageIO;
import toolset.imagetools.BufferedImageToFXImage;

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

    private AnchorPane thisAnchorPane;
    private Notifier notifier;

    /**
     * Initializes imageInfoViewController with its file and its AnchorPane
     * @param file
     * @param anchorPane
     */
    public void initialize(File file, AnchorPane anchorPane) {
        if (file == null) throw new IllegalArgumentException();
        thisAnchorPane = anchorPane;

        nameValue.setText(file.getName());
        getAndFormatFileSize(file);

        addNotifierToMakeImageFitWindow();

        loadImageInBackground(file);
    }

    private void getAndFormatFileSize(File file) {
        double length = file.length();
        final int unit = 1024;
        if (length < unit * unit) {
            sizeValue.setText(String.format("%.3f", length/unit) + "kB");
        } else  {
            sizeValue.setText(String.format("%.3f", length / unit / unit) + "MB");
        }
    }

    private void addNotifierToMakeImageFitWindow() {
        notifier = (width, height) -> {
            thisAnchorPane.setPrefWidth(width/2);
            thisAnchorPane.setMaxWidth(width/2);

            if (bufferedImage == null) return;

            final int HEIGHT_DIFFERENCE = 460; //const that is "just okay", to calculate height of images.

            double ratio = (bufferedImage.getWidth() * 1.0)/(1.0 * bufferedImage.getHeight());

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
    }

    private void loadImageInBackground(File resourceFile) {
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

    public void removeItsNotifier() {
        MainViewController.removeNotifier(notifier);
    }

}
