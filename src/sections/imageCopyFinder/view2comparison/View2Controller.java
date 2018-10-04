package sections.imageCopyFinder.view2comparison;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import sections.imageCopyFinder.ComparableImage;
import sections.imageCopyFinder.ComparableImagePair;
import sections.imageCopyFinder.ImageCopyFinder;
import sections.imageCopyFinder.imageInfoView.ImageInfoView;
import sections.imageCopyFinder.imageInfoView.ImageInfoViewController;

import java.util.ArrayList;

import static toolset.FileManagementTools.moveFile;

public final class View2Controller {

    private ArrayList<ComparableImagePair> imagePairs;
    private ComparableImagePair hoveredElement;
    private String deleteLocation;


    @FXML
    private ListView<ComparableImagePair> comparableImagePairListView;

    @FXML
    private Slider sliderPercentIdentical;

    @FXML
    private AnchorPane leftImageAnchorPane;

    @FXML
    private AnchorPane rightImageAnchorPane;

    private double lastSimilarity;

    @FXML
    void deleteLeftPress(ActionEvent event) {
        int index = getSelectedItemIndex();
        delete(hoveredElement.getComparableImageLeft());
        selectItemAtIndex(index);
    }

    @FXML
    void deleteRightPress(ActionEvent event) {
        int index = getSelectedItemIndex();
        delete(hoveredElement.getComparableImageRight());
        selectItemAtIndex(index);
    }

    @FXML
    void hideButtonPress(ActionEvent event) {
        int index = getSelectedItemIndex();
        imagePairs.remove(hoveredElement);
        displayPairsWithSimilarityOver(lastSimilarity);
        selectItemAtIndex(index);
    }


    @FXML
    public void initialize() {
        deleteLocation = ImageCopyFinder.getDeleteDirectory();

        sliderPercentIdentical.valueProperty().addListener((observable, oldValue, newValue) -> {
            double x = sliderPercentIdentical.getValue() / 100.0;
            displayPairsWithSimilarityOver(x);
            refreshListView();
        });

        comparableImagePairListView.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(ComparableImagePair comparableImagePair, boolean empty) {
                        super.updateItem(comparableImagePair, empty);

                        if (empty || comparableImagePair == null) {
                            setText(null);
                        } else {
                            setText(comparableImagePair.toString());
                        }
                    }
                }
        );
        imagePairs = ImageCopyFinder.getImageComparator().getImagePairs();
        displayPairsWithSimilarityOver(0.0); //all of them
        comparableImagePairListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> elementHovered(newValue)
        );
    }

    private int getSelectedItemIndex() {
        return comparableImagePairListView.getSelectionModel().getSelectedIndex();
    }

    private int listViewSize() {
        return comparableImagePairListView.getItems().size();
    }

    private void selectItemAtIndex(int i) {
        if (i < listViewSize()) {
            comparableImagePairListView.getSelectionModel().select(i);
        } else {
            if (i >= 0 && listViewSize() > 0) {
                selectItemAtIndex(i - 1);
            }
        }

    }

    private void delete(ComparableImage cip) {
        for (int i = 0; i < imagePairs.size(); i++) {
            if (imagePairs.get(i).isInPair(cip)) {
                imagePairs.remove(i);
                i--;
            }
        }
        displayPairsWithSimilarityOver(lastSimilarity);
        moveFile(cip.getFile(), deleteLocation);
    }

    private void displayPairsWithSimilarityOver(double value) {
        lastSimilarity = value;
        ArrayList<ComparableImagePair> displayedImagePairs = new ArrayList<>();
        for (ComparableImagePair imagePair : imagePairs) {
            if (imagePair.getSimilarity() >= value) {
                displayedImagePairs.add(imagePair);
            }
        }
        ObservableList<ComparableImagePair> comparableImagePairs = FXCollections.observableArrayList();
        comparableImagePairs.addAll(displayedImagePairs);
        comparableImagePairListView.setItems(comparableImagePairs);
        refreshListView();
    }

    private void refreshListView() {
        comparableImagePairListView.refresh();
    }

    private void elementHovered(ComparableImagePair comparableImagePair) {
        if (comparableImagePair == null) return;
        if (hoveredElement == comparableImagePair) return;
        hoveredElement = comparableImagePair;
        setNewImageAnchorPanes(comparableImagePair);
    }



    private ArrayList<ImageInfoViewController> oldNotifiers = new ArrayList();

    //after selecting ComparableImagePair, the AnchorPanes on the bottom will be updated
    private void setNewImageAnchorPanes(ComparableImagePair comparableImagePair) {
        removeOldNotifiers();

        setImageAnchorPane(leftImageAnchorPane, comparableImagePair.getComparableImageLeft());
        setImageAnchorPane(rightImageAnchorPane, comparableImagePair.getComparableImageRight());
    }

    private void removeOldNotifiers() {
        for (var imageInfoViewController : oldNotifiers) {
            imageInfoViewController.removeItsNotifier();
        }
        oldNotifiers.clear();
    }

    private void setImageAnchorPane(AnchorPane anchorPane, ComparableImage comparableImage) {
        var imageInfoView = new ImageInfoView();
        var infoViewController = imageInfoView.getController();
        infoViewController.initialize(comparableImage.getFile());
        anchorPane.getChildren().setAll((Node) imageInfoView.getUserInterface());
        oldNotifiers.add(infoViewController);
    }


}
