package sections.imageCopyFinder.view1;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    private ImageComparator imageComparator = null;


    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            imageSizeComboBox.getItems().addAll(4, 8, 16, 32, 64, 128, 256, 512);
        });
    }

    @FXML
    private void runButtonPress(ActionEvent event) {
        TaskImageComparator tic = new TaskImageComparator(folderLocationTextField.getText(), imageSizeComboBox.getValue());
        tic.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                event1 -> {
                    imageComparator = tic.getValue();
                    finished();
                }
        );

        new Thread(tic).start();
    }

    private void finished() {
        if (imageComparator == null) {
            throw new RuntimeException("UnexpectedError: imageComparator not initialized");
        }
        if (imageComparator.getStatus() == ImageComparator.ImageComparatorStatus.SUCCESFUL) {
            ImageCopyFinder.setImageComparator(imageComparator);
            ImageCopyFinder.setDeleteDirectory(folderLocationTextField.getText() + System.getProperty("file.separator") + "markedForDeletion" + System.getProperty("file.separator"));
            MainViewController.imageCopyFinder.setInterface(ImageCopyFinder.ImageCopyFinderViews.compareCopiedImagesView);
            MainViewController.reloadView();
        } else {
            System.out.println(imageComparator.getStatus());
        }


    }
}
