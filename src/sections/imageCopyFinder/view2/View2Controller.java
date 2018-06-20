package sections.imageCopyFinder.view2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import sections.imageCopyFinder.ComparableImage;
import sections.imageCopyFinder.ComparableImagePair;
import sections.imageCopyFinder.ImageCopyFinder;
import sections.imageCopyFinder.imageInfoView.ImageInfoView;
import sections.imageCopyFinder.imageInfoView.ImageInfoViewController;

import java.io.File;
import java.util.ArrayList;

import static universal.tools.FileManagementTools.moveFile;

public class View2Controller {

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

    @FXML
    private SplitPane splitPane;

    private double lastSimilarity;

    @FXML
    void deleteLeftPress(ActionEvent event) {
        System.out.println("left");
        delete(hoveredElement.getComparableImage1());
    }

    @FXML
    void deleteRightPress(ActionEvent event) {
        System.out.println("right");
        delete(hoveredElement.getComparableImage2());
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

    @FXML
    void hideButtonPress(ActionEvent event) {
        System.out.println("hide");
        for (int i = 0; i < imagePairs.size(); i++) {
            if (hoveredElement.equals(imagePairs.get(i))) {
                imagePairs.remove(i);
                displayPairsWithSimilarityOver(lastSimilarity);
                return;
            }
        }
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
        System.out.println(comparableImagePair.getComparableImage1().getFile().getName() + ", " + comparableImagePair.getComparableImage2().getFile().getName());
        setImageAnchorPanes(comparableImagePair);
    }


    private void setImageAnchorPanes(ComparableImagePair comparableImagePair) {
        ImageInfoView im1;
        ImageInfoView im2;

        ImageInfoViewController imc1;
        ImageInfoViewController imc2;

        im1 = new ImageInfoView();
        im2 = new ImageInfoView();

        imc1 = im1.getController();
        imc2 = im2.getController();

        imc1.setFileInformation(comparableImagePair.getComparableImage1().getFile());
        imc2.setFileInformation(comparableImagePair.getComparableImage2().getFile());

        leftImageAnchorPane.getChildren().setAll((Node) im1.getUserInterface());
        rightImageAnchorPane.getChildren().setAll((Node) im2.getUserInterface());

    }


}
