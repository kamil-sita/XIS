package sections.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import sections.imageCopyFinder.ImageCopyFinder;
import sections.welcomePage.WelcomePage;


public class MainViewController {

    private static Label labelStatusGlobal;
    private static AnchorPane vistaHolderGlobal;
    private static AnchorPane currentVistaGlobal;
    private static ScrollPane scrollPaneGlobal;

    private static AnchorPane anchorPaneLeftGlobal;


    @FXML
    private AnchorPane vistaHolder;

    @FXML
    private AnchorPane anchorPaneLeft;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label labelStatus;

    @FXML
    void mainPress(ActionEvent event) {
        WelcomePage welcomePage = new WelcomePage();
        AnchorPane anchorPane = welcomePage.getUserInterface();
        changeVista(anchorPane);
        setStatus("welcome page loaded");
    }

    @FXML
    void imageCopyFinderPress(ActionEvent event) {
        ImageCopyFinder imageCopyFinder = new ImageCopyFinder();
        AnchorPane anchorPane = imageCopyFinder.getUserInterface();
        changeVista(anchorPane);
        setStatus("imageCopyFinder module loaded");
    }

    @FXML
    public void initialize() {
        labelStatusGlobal = labelStatus;
        vistaHolderGlobal = vistaHolder;
        anchorPaneLeftGlobal = anchorPaneLeft;
        scrollPaneGlobal = scrollPane;
        mainPress(null);
        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            MainViewController.onWindowSizeChange();
        });
        onWindowSizeChange();
    }

    public static void changeVista(AnchorPane anchorPane) {
        currentVistaGlobal = anchorPane;
        vistaHolderGlobal.getChildren().setAll((Node) anchorPane);
        onWindowSizeChange();
    }

    public static void setStatus(String text) {
        labelStatusGlobal.setText(text);
    }

    public static void onWindowSizeChange() {
        resizeAnchorPane();
    }

    //changing size of anchorPane (vista holder) in scrollPane
    private static void resizeAnchorPane() {
        if (currentVistaGlobal == null) return;
        currentVistaGlobal.setPrefWidth(scrollPaneGlobal.getViewportBounds().getWidth());
    }
}
