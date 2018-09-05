package sections.imageCopyFinder.imageInfoView;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sections.Notifier;
import sections.NotifierFactory;
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
        notifier = NotifierFactory.scalingImageNotifier(bufferedImage, imageView, 460,50, 2.0);
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
