package sections.imageCopyFinder.view1;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import sections.imageCopyFinder.ImageComparator;
import sections.imageCopyFinder.ImageCopyFinder;
import sections.imageCopyFinder.ThreadedImageComparator;
import sections.main.MainViewController;

import java.util.stream.StreamSupport;

public class View1Controller {

    @FXML
    private TextField folderLocationTextField;

    private ImageComparator imageComparator = null;

    @FXML
    private void runButtonPress(ActionEvent event) {

        ThreadedImageComparator tic = new ThreadedImageComparator(folderLocationTextField.getText());

        tic.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        imageComparator = tic.getValue();
                        finished();

                    }
                }
                );

        new Thread(tic).start();

        System.out.println("press");

    }

    private void finished() {
        if (imageComparator == null) {
            throw new RuntimeException("UnexpectedError");
        }

        if (imageComparator.getStatus() == ImageComparator.ImageComparatorStatus.SUCCESFUL) {
            MainViewController.imageCopyFinder.setInterface(ImageCopyFinder.ImageCopyFinderViews.compareCopiedImagesView);
            MainViewController.reloadView();
        } else {
            System.out.println(imageComparator.getStatus());
        }


    }
}
