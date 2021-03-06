package XIS.sections.imagecopyfinder.view2comparison;

import XIS.sections.UserFeedback;
import XIS.sections.XisController;
import XIS.sections.imagecopyfinder.ComparedImage;
import XIS.sections.imagecopyfinder.ComparedImagePair;
import XIS.sections.imagecopyfinder.ImageCopyFinderModuleVista;
import XIS.sections.imagecopyfinder.SerializableIcfState;
import XIS.sections.imagecopyfinder.imageinfoview.ImageInfoView;
import XIS.sections.imagecopyfinder.imageinfoview.ImageInfoViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static XIS.toolset.FileManagementTools.moveFile;

public final class View2Controller extends XisController {

    private List<ComparedImagePair> imagePairs;
    private ComparedImagePair hoveredElement;
    private String deleteLocation;
    private String inputCommand;
    private int imageInputSize;

    @FXML
    private ListView<ComparedImagePair> comparableImagePairListView;

    @FXML
    private Slider sliderPercentIdentical;

    @FXML
    private StackPane leftPane;

    @FXML
    private StackPane rightPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label numberOfImages;


    private double lastSimilarity;

    @FXML
    void serializePress(ActionEvent event) {
        SerializableIcfState icfState = new SerializableIcfState(imagePairs, inputCommand, imageInputSize, deleteLocation);
        boolean result = SerializableIcfState.serialize(icfState);
        if (result) {
            UserFeedback.getInstance().popup("Saved successfully!");
        } else {
            UserFeedback.getInstance().popup("Could not save!");
        }
    }


    @FXML
    void deleteLeftPress(ActionEvent event) {
        int index = getSelectedItemIndex();
        delete(hoveredElement.getImageOnLeft());
        selectItemAtIndex(index);
    }

    @FXML
    void deleteRightPress(ActionEvent event) {
        int index = getSelectedItemIndex();
        delete(hoveredElement.getImageOnRight());
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

        deleteLocation = ImageCopyFinderModuleVista.getDeleteDirectory();
        imageInputSize = ImageCopyFinderModuleVista.getImageSize();
        inputCommand = ImageCopyFinderModuleVista.getInputString();

        sliderPercentIdentical.valueProperty().addListener((observable, oldValue, newValue) -> {
            double x = sliderPercentIdentical.getValue() / 100.0;
            displayPairsWithSimilarityOver(x);
            refreshListView();
        });

        comparableImagePairListView.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(ComparedImagePair comparedImagePair, boolean empty) {
                        super.updateItem(comparedImagePair, empty);
                        if (empty || comparedImagePair == null) {
                            setText(null);
                        } else {
                            setText(comparedImagePair.toString());
                        }
                    }
                }
        );
        imagePairs = ImageCopyFinderModuleVista.getImagePairList();
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

    private void delete(ComparedImage cip) {
        if (moveFile(cip.getFile(), deleteLocation, (mainInformation, reporter) -> getUserFeedback().popup(mainInformation))) {
            imagePairs = imagePairs
                    .stream()
                    .filter(image -> !image.isInPair(cip))
                    .collect(Collectors.toList());
            displayPairsWithSimilarityOver(lastSimilarity);
        }
    }

    private void displayAllImages() {
        displayPairsWithSimilarityOver(0);
    }

    private void displayPairsWithSimilarityOver(double value) {
        lastSimilarity = value;
        ArrayList<ComparedImagePair> displayedImagePairs = new ArrayList<>();
        for (ComparedImagePair imagePair : imagePairs) {
            if (imagePair.getSimilarity() >= value) {
                displayedImagePairs.add(imagePair);
            }
        }
        ObservableList<ComparedImagePair> comparedImagePairs = FXCollections.observableArrayList();
        comparedImagePairs.addAll(displayedImagePairs);
        comparableImagePairListView.setItems(comparedImagePairs);
        refreshListView();
    }

    private void refreshListView() {
        comparableImagePairListView.refresh();
        numberOfImages.setText(comparableImagePairListView.getItems().size() + " images at current view");
    }

    private void elementHovered(ComparedImagePair comparedImagePair) {
        if (comparedImagePair == null) return;
        if (hoveredElement == comparedImagePair) return;
        hoveredElement = comparedImagePair;
        setNewImageAnchorPanes(comparedImagePair);
    }



    private ArrayList<ImageInfoViewController> oldNotifiers = new ArrayList<>();

    //after selecting ComparedImagePair, the AnchorPanes on the bottom will be updated
    private void setNewImageAnchorPanes(ComparedImagePair comparedImagePair) {
        removeOldNotifiers();
        setImageAnchorPane(leftPane, comparedImagePair.getImageOnLeft());
        setImageAnchorPane(rightPane, comparedImagePair.getImageOnRight());

    }

    private void removeOldNotifiers() {
        for (var imageInfoViewController : oldNotifiers) {
            imageInfoViewController.removeItsNotifier();
        }
        oldNotifiers.clear();
    }

    private void setImageAnchorPane(Pane anchorPane, ComparedImage comparedImage) {
        var imageInfoView = new ImageInfoView();
        var infoViewController = imageInfoView.getController();
        infoViewController.initialize(comparedImage.getFile());
        anchorPane.getChildren().setAll((Node) imageInfoView.getUserInterface());
        oldNotifiers.add(infoViewController);
    }


}
