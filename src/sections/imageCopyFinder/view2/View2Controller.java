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
import sections.imageCopyFinder.ComparableImagePair;
import sections.imageCopyFinder.ImageCopyFinder;
import sections.imageCopyFinder.imageInfoView.ImageInfoView;
import sections.imageCopyFinder.imageInfoView.ImageInfoViewController;

import java.util.ArrayList;

public class View2Controller {

    private ArrayList<ComparableImagePair> imagePairs;
    private ArrayList<ComparableImagePair> displayedImagePairs;
    private ComparableImagePair hoveredElement;

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

    @FXML
    void deleteLeftPress(ActionEvent event) {

    }

    @FXML
    void deleteRightPress(ActionEvent event) {

    }

    @FXML
    void hideButtonPress(ActionEvent event) {

    }


    @FXML
    public void initialize() {

        sliderPercentIdentical.valueProperty().addListener((observable, oldValue, newValue) -> {
            double x = sliderPercentIdentical.getValue()/100.0;
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
        displayedImagePairs = new ArrayList<>();
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
