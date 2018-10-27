package sections.imagecopyfinder.xcxc;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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

    @FXML
    private Label parentName;

    private Notifier notifier;

    /**
     * Initializes imageInfoViewController with its file and its AnchorPane
     * @param file
     */
    public void initialize(File file) {
        if (file == null) throw new IllegalArgumentException();

        nameValue.setText(file.getName());
        setParentName(file);
        getAndFormatFileSize(file);

        addNotifierToMakeImageFitWindow();

        loadImageInBackground(file);
    }

    private void setParentName(File file) {
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            parentName.setText("Can't retrieve");
        } else {
            parentName.setText(parentFile.getName());
        }
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
        notifier = NotifierFactory.scalingImageNotifier(bufferedImage, imageView, 460,50, 0.5);
        MainViewController.addNotifier(notifier);
        MainViewController.reloadView();
    }

    private void loadImageInBackground(File resourceFile) {
        new Thread(() -> {
            var optionalImage = BufferedImageIO.getImage(resourceFile);
            if (!optionalImage.isPresent()) return;
            bufferedImage = optionalImage.get();
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
