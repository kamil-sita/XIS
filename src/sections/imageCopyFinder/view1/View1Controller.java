package sections.imageCopyFinder.view1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import sections.imageCopyFinder.ImageCopyFinder;
import sections.main.MainViewController;

import java.util.stream.StreamSupport;

public class View1Controller {
    @FXML
    private Spinner<Integer> imageSizeSpinner;

    @FXML
    void runButtonPress(ActionEvent event) {
        System.out.println("press");
        MainViewController.imageCopyFinder.setInterface(ImageCopyFinder.ImageCopyFinderViews.compareCopiedImagesView);
        MainViewController.reloadView();
    }
}
