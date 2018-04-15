package sections.imageCopyFinder.view2;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import sections.imageCopyFinder.ComparableImage;
import sections.imageCopyFinder.ComparableImagePair;
import sections.imageCopyFinder.ImageCopyFinder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

public class View2Controller {

    private ArrayList<ComparableImagePair> imagePairs;

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
            ArrayList<ComparableImagePair> newImagePairs = imagePairsWithSimilarityOver(x);
            refreshListView(newImagePairs);
            refreshListView();
        });

        comparableImagePairListView.setCellFactory(param -> new ListCell<ComparableImagePair>() {
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
        refreshListView(imagePairs);
        comparableImagePairListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    elementHovered(newValue);
        });
    }

    private ArrayList<ComparableImagePair> imagePairsWithSimilarityOver (double value) {
        ArrayList<ComparableImagePair> newImagePairs = new ArrayList<>();
        for (ComparableImagePair imagePair : imagePairs) {
            if (imagePair.getSimilarity() >= value) {
                newImagePairs.add(imagePair);
            }
        }
        return newImagePairs;
    }

    private void refreshListView() {
        comparableImagePairListView.refresh();
    }

    private void refreshListView(ArrayList<ComparableImagePair> pairs) {
        ObservableList<ComparableImagePair> comparableImagePairs = FXCollections.observableArrayList();
        comparableImagePairs.addAll(pairs);
        comparableImagePairListView.setItems(comparableImagePairs);
        refreshListView();
    }

    private void elementHovered(ComparableImagePair comparableImagePair) {
        System.out.println(comparableImagePair.getComparableImage1().getFile().getName() + ", " + comparableImagePair.getComparableImage2().getFile().getName());
    }


}
