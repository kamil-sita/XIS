package sections.imagecopyfinder.view2comparison;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import sections.XisController;
import sections.imagecopyfinder.ComparableImage;
import sections.imagecopyfinder.ComparableImagePair;
import sections.imagecopyfinder.ImageCopyFinder;
import sections.imagecopyfinder.imageinfoview.ImageInfoView;
import sections.imagecopyfinder.imageinfoview.ImageInfoViewController;

import java.util.ArrayList;
import java.util.List;

import static toolset.FileManagementTools.moveFile;

public final class View2Controller extends XisController {

    private List<ComparableImagePair> imagePairs;
    private ComparableImagePair hoveredElement;
    private String deleteLocation;

    @FXML
    private ListView<ComparableImagePair> comparableImagePairListView;

    @FXML
    private Slider sliderPercentIdentical;

    @FXML
    private StackPane leftPane;

    @FXML
    private StackPane rightPane;

    @FXML
    private GridPane gridPane;

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

        registerNotifier((width, height) -> {
            gridPane.setPrefHeight(height - 300);
        });

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
        displayAllImages();
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
        moveFile(cip.getFile(), deleteLocation, getUserFeedback());
    }

    private void displayAllImages() {
        displayPairsWithSimilarityOver(0);
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
        System.out.println(comparableImagePair.toString() + " : " + comparableImagePair.getSimilarity());
        hoveredElement = comparableImagePair;
        setNewImageAnchorPanes(comparableImagePair);
    }



    private ArrayList<ImageInfoViewController> oldNotifiers = new ArrayList<>();

    //after selecting ComparableImagePair, the AnchorPanes on the bottom will be updated
    private void setNewImageAnchorPanes(ComparableImagePair comparableImagePair) {
        removeOldNotifiers();
        setImageAnchorPane(leftPane, comparableImagePair.getComparableImageLeft());
        setImageAnchorPane(rightPane, comparableImagePair.getComparableImageRight());

    }

    private void removeOldNotifiers() {
        for (var imageInfoViewController : oldNotifiers) {
            imageInfoViewController.removeItsNotifier();
        }
        oldNotifiers.clear();
    }

    private void setImageAnchorPane(Pane anchorPane, ComparableImage comparableImage) {
        var imageInfoView = new ImageInfoView();
        var infoViewController = imageInfoView.getController();
        infoViewController.initialize(comparableImage.getFile());
        anchorPane.getChildren().setAll((Node) imageInfoView.getUserInterface());
        oldNotifiers.add(infoViewController);
    }


}
