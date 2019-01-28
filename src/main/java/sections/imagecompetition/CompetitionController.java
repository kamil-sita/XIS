package sections.imagecompetition;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import sections.Notifier;
import sections.NotifierFactory;
import sections.UserFeedback;
import sections.main.MainViewController;
import toolset.JavaFXTools;
import toolset.io.BufferedImageIO;
import toolset.io.MultipleFileIO;

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public final class CompetitionController {

    private Notifier notifierL;
    private Notifier notifierR;

    private BufferedImage imageL;
    private BufferedImage imageR;
    private ImageContestant imageContestantL;
    private ImageContestant imageContestantR;


    private List<ImageContestant> contestants;

    @FXML
    private ImageView imageViewL;

    @FXML
    private ImageView imageViewR;


    @FXML
    private ListView<ImageContestant> listView;

    @FXML
    void newCompetitionPress(ActionEvent event) {
        var optional = UserFeedback.getText("Competition creator", "Enter details", "File location:");
        System.out.println(optional.get());
        String[] path = {optional.get()};
        var folders = MultipleFileIO.getFoldersFromStrings(path);
        var files = MultipleFileIO.loadFilesFromFolders(folders);
        files = MultipleFileIO.filterOutNonImages(files);
        contestants = ImageContestant.createImageContestants(files);
        updateListView();
    }

    @FXML
    void loadPress(ActionEvent event) {

    }

    @FXML
    void savePress(ActionEvent event) {

    }

    private void updateListView() {
        contestants.sort(new Comparator<ImageContestant>() {
            @Override
            public int compare(ImageContestant o1, ImageContestant o2) {
                return Double.compare(o1.getRating(), o2.getRating());
            }
        });
        listView.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(ImageContestant imageContestant, boolean empty) {
                        super.updateItem(imageContestant, empty);
                        if (empty || imageContestant == null) {
                            setText(null);
                        } else {
                            setText(imageContestant.getName());
                        }
                    }
                }
        );
        ObservableList<ImageContestant> observableContestants = FXCollections.observableArrayList();
        observableContestants.addAll(contestants);
        listView.setItems(observableContestants);
        listView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayLeft(newValue)
        );

    }

    private void displayLeft(ImageContestant ic) {
        imageContestantL = ic;
        imageL = getContestantImage(ic);
        var fxImage = JavaFXTools.toFxImage(imageL);
        imageViewL.setImage(fxImage);
        reAddNotifier();
        displayRandomRight();
    }

    private void reAddNotifier() {
        MainViewController.removeNotifier(notifierL);
        MainViewController.removeNotifier(notifierR);
        notifierL = NotifierFactory.scalingImageNotifier(imageL, imageViewL, 50, 250, 0.5);
        notifierR = NotifierFactory.scalingImageNotifier(imageR, imageViewR, 50, 250, 0.5);
        MainViewController.addNotifier(notifierL);
        MainViewController.addNotifier(notifierR);
        MainViewController.forceOnWindowSizeChange();
    }

    private void displayRandomRight() {
        if (contestants.size() <= 1) return;
        var randomContestant = getRandom();
        while (randomContestant == imageContestantL) {
            randomContestant = getRandom();
        }
        imageContestantR = randomContestant;
        imageR = getContestantImage(imageContestantR);
        var fxImage = JavaFXTools.toFxImage(imageR);
        imageViewR.setImage(fxImage);
        reAddNotifier();
    }

    private ImageContestant getRandom() {
        return contestants.get(new Random().nextInt(contestants.size()));
    }

    private static BufferedImage getContestantImage(ImageContestant ic) {
        var file = ic.getImageFile();
        return BufferedImageIO.getImage(file).get();
    }

}
