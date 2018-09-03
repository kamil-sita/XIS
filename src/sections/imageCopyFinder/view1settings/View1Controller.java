package sections.imageCopyFinder.view1settings;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import sections.imageCopyFinder.ImageComparator;
import sections.imageCopyFinder.ImageCopyFinder;
import sections.imageCopyFinder.TaskImageComparator;
import sections.main.MainViewController;

public final class View1Controller {

    @FXML
    private TextField folderLocationTextField;

    @FXML
    private ComboBox<Integer> imageSizeComboBox;

    @FXML
    private CheckBox checkBoxGeometricalMode;

    private ImageComparator imageComparator = null;


    @FXML
    public void initialize() {
        addImageSizes();
    }

    private void addImageSizes() {
        Platform.runLater(() -> {
            //adding scaling option and selecting default
            imageSizeComboBox.getItems().addAll(4, 8, 16, 32, 64, 128, 256, 512); //this line will throw exception, not sure why
            imageSizeComboBox.getSelectionModel().select(6);
        });
    }

    @FXML
    private void runButtonPress(ActionEvent event) {
        TaskImageComparator tic = new TaskImageComparator(
                folderLocationTextField.getText(),
                imageSizeComboBox.getValue(),
                checkBoxGeometricalMode.isSelected()
        );
        tic.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                event1 -> {
                    imageComparator = tic.getValue();
                    onFinish();
                }
        );

        new Thread(tic).start();
    }

    private void onFinish() {
        if (imageComparator == null) {
            throw new RuntimeException("UnexpectedError: imageComparator not initialized");
        }
        if (imageComparator.getStatus() == ImageComparator.ImageComparatorStatus.SUCCESSFUL) {
            ImageCopyFinder.setImageComparator(imageComparator);
            ImageCopyFinder.setDeleteDirectory(folderLocationTextField.getText() + System.getProperty("file.separator") + "markedForDeletion" + System.getProperty("file.separator"));
            MainViewController.imageCopyFinder.setInterface(ImageCopyFinder.ImageCopyFinderViews.compareCopiedImagesView);
            MainViewController.reloadView();
        } else {
            System.out.println(imageComparator.getStatus());
        }


    }
}
