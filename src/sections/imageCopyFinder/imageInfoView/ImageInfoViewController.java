package sections.imageCopyFinder.imageInfoView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import sections.imageCopyFinder.ImageCopyFinder;
import sections.main.MainViewController;

public class ImageInfoViewController {
    @FXML
    private Spinner<Integer> imageSizeSpinner;

    @FXML
    void runButtonPress(ActionEvent event) {
        System.out.println("press");
        MainViewController.imageCopyFinder.setInterface(ImageCopyFinder.ImageCopyFinderViews.compareCopiedImagesView);
        MainViewController.reloadView();
    }
}
