package sections.imagecopyfinder.view1settings;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sections.UserFeedback;
import sections.imagecopyfinder.ImageComparator;
import sections.imagecopyfinder.ImageCopyFinder;
import sections.imagecopyfinder.TaskImageComparator;
import sections.main.MainViewController;

public final class View1Controller {

    @FXML
    private TextArea folderLocations;

    @FXML
    private ComboBox<Integer> imageSizeComboBox;

    @FXML
    private CheckBox checkBoxGeometricalMode;

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
        TaskImageComparator tic = new TaskImageComparator(
                folderLocations.getText().split("\n"),
                imageSizeComboBox.getValue(),
                checkBoxGeometricalMode.isSelected()
        );
        tic.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                event1 -> {
                    var imageComparator = tic.getValue();
                    onFinish(imageComparator);
                }
        );

        new Thread(tic).start();
    }

    private void onFinish(ImageComparator imageComparator) {
        if (imageComparator.getStatus() == ImageComparator.ImageComparatorStatus.SUCCESSFUL) {
            ImageCopyFinder.setImageComparator(imageComparator);
            ImageCopyFinder.setDeleteDirectory(deleteFolder.toString());
            MainViewController.imageCopyFinder.setInterface(ImageCopyFinder.ImageCopyFinderViews.compareCopiedImagesView);
            MainViewController.refreshVista();
        } else {
            UserFeedback.popup(imageComparator.getStatus().toString());
        }
    }
}
