package sections.imagecopyfinder.view1settings;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sections.Interruptible;
import sections.OneBackgroundJobManager;
import sections.XisController;
import sections.imagecopyfinder.FolderImageComparator;
import sections.imagecopyfinder.ImageComparator;
import sections.imagecopyfinder.ImageCopyFinder;

public final class View1Controller extends XisController {

    @FXML
    private TextArea folderLocations;

    @FXML
    private ComboBox<Integer> imageSizeComboBox;

    @FXML
    private TextField deleteFolder;


    @FXML
    public void initialize() {
        addImageSizes();
    }

    private void addImageSizes() {
        Platform.runLater(() -> {
            //adding scaling option and selecting default
            try {
                imageSizeComboBox.getItems().addAll(4, 8, 16, 32, 64, 128, 256, 512); //this line will throw exception, not sure why
                imageSizeComboBox.getSelectionModel().select(6);
            } catch (Exception e) {
                //
            }
        });
    }

    @FXML
    private void runButtonPress(ActionEvent event) {
        final String JAVAFX_NEWLINE = "\n";

        final var folderComparator = new FolderImageComparator(folderLocations.getText().split(JAVAFX_NEWLINE),
                imageSizeComboBox.getValue()
        );

        OneBackgroundJobManager.setAndRunJob(new Interruptible() {
            private ImageComparator imageComparator;

            @Override
            public Runnable getRunnable() {
                return () -> imageComparator = folderComparator.compare(this);
            }

            @Override
            public Runnable onUninterruptedFinish() {
                return () -> {if (imageComparator != null) onFinish(imageComparator);};
            }
        });

    }

    private void onFinish(ImageComparator imageComparator) {
        if (imageComparator.getStatus() == ImageComparator.ImageComparatorStatus.SUCCESSFUL) {
            ImageCopyFinder.setImageComparator(imageComparator);
            ImageCopyFinder.setDeleteDirectory(deleteFolder.getText());
            getMainViewController().getImageCopyFinder().setInterface(ImageCopyFinder.ImageCopyFinderViews.compareCopiedImagesView);
            refreshVista();
        } else {
            getUserFeedback().popup(imageComparator.getStatus().toString());
        }
    }
}
