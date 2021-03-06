package XIS.sections.imagecopyfinder.imageinfoview;

import XIS.sections.NotifierFactory;
import XIS.sections.XisController;
import XIS.toolset.JavaFXTools;
import XIS.toolset.io.BufferedImageIO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.awt.image.BufferedImage;
import java.io.File;

public final class ImageInfoViewController extends XisController {
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

    private File file;

    /**
     * Initializes imageInfoViewController with its file and its AnchorPane
     * @param file
     */
    public void initialize(File file) {
        if (file == null) throw new IllegalArgumentException();

        this.file = file;
        nameValue.setText(file.getName());
        setParentName(file);
        getAndFormatFileSize(file);

        loadImageInBackground(file);
    }

    @FXML
    void clickedImage(MouseEvent event) {
        getUserFeedback().openInDefault(file);
    }


    private void setParentName(File file) {
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            parentName.setText("Unknown");
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
        var notifier = NotifierFactory.scalingImageNotifier(bufferedImage, imageView, 400,50, 0.5);
        registerNotifier(notifier);
        refreshVista();
    }

    private void loadImageInBackground(File resourceFile) {
        new Thread(() -> {
            var optionalImage = BufferedImageIO.getImage(resourceFile);
            if (!optionalImage.isPresent()) return;
            bufferedImage = optionalImage.get();
            Platform.runLater(() -> {
                imageView.setImage(JavaFXTools.toFxImage(bufferedImage));
                dimensionsValue.setText("(" + bufferedImage.getWidth() + "x" + bufferedImage.getHeight() + ")");
                refreshVista();
                addNotifierToMakeImageFitWindow();

            });

        }).start();
    }

    public void removeItsNotifier() {
        deregisterAllNotifiers();
    }


}
