package XIS.sections.imagecopyfinder.view1settings;

import XIS.sections.Interruptible;
import XIS.sections.SingleJobManager;
import XIS.sections.UserFeedback;
import XIS.sections.XisController;
import XIS.sections.imagecopyfinder.FolderImageComparator;
import XIS.sections.imagecopyfinder.ImageComparator;
import XIS.sections.imagecopyfinder.ImageCopyFinderModuleVista;
import XIS.sections.imagecopyfinder.SerializableIcfState;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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

        SingleJobManager.setAndRunJob(new Interruptible() {
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


    @FXML
    void restoreOldSettingsPress(ActionEvent event) {
        var state = getState();
        if (state == null) {
            UserFeedback.getInstance().popup("Could not load state!");
            return;
        }
        folderLocations.setText(state.getInputCommand());
        deleteFolder.setText(state.getInputCommandDeleteLocation());
        int index = imageSizeComboBox.getItems().indexOf(state.getInputImageSize());
        if (index < 0) index = 0;
        imageSizeComboBox.getSelectionModel().select(index);

    }

    @FXML
    void restoreOldStatePress(ActionEvent event) {
        var state = getState();
        if (state == null) {
            UserFeedback.getInstance().popup("Could not load state!");
            return;
        }

        ImageCopyFinderModuleVista.setImagePairList(state.getImagePairs());
        ImageCopyFinderModuleVista.setDeleteDirectory(state.getInputCommandDeleteLocation());
        ImageCopyFinderModuleVista.setImageSize(state.getInputImageSize());
        ImageCopyFinderModuleVista.setInputString(state.getInputCommand());

        getMainViewController().getImageCopyFinder().setInterface(ImageCopyFinderModuleVista.ImageCopyFinderViews.compareCopiedImagesView);
        refreshVista();
    }

    private SerializableIcfState getState() {
        return SerializableIcfState.deserialize();
    }

    private void onFinish(ImageComparator imageComparator) {
        if (imageComparator.getStatus() == ImageComparator.ImageComparatorStatus.SUCCESSFUL) {
            ImageCopyFinderModuleVista.setImagePairList(imageComparator.getImagePairs());
            ImageCopyFinderModuleVista.setDeleteDirectory(deleteFolder.getText());
            ImageCopyFinderModuleVista.setImageSize(imageSizeComboBox.getValue());
            ImageCopyFinderModuleVista.setInputString(folderLocations.getText());

            getMainViewController().getImageCopyFinder().setInterface(ImageCopyFinderModuleVista.ImageCopyFinderViews.compareCopiedImagesView);
            refreshVista();
        } else {
            if (imageComparator.getStatus() == ImageComparator.ImageComparatorStatus.ERROR_ALREADY_HANDLED) return;
            getUserFeedback().popup(imageComparator.getStatus().toString());
        }
    }
}
