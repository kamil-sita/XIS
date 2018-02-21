package sections.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import sections.imageCopyFinder.ImageCopyFinder;


public class ControllerMV {

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
        windowSizeChange();
    }

    public static void changeVista(AnchorPane anchorPane) {
        currentVistaGlobal = anchorPane;
        vistaHolderGlobal.getChildren().setAll((Node) anchorPane);
        windowSizeChange();
    }

    public static void setStatus(String text) {
        labelStatusGlobal.setText(text);
    }

    public static void windowSizeChange() {
        generateShadow();
        resizeAnchorPane();
    }

    private static void generateShadow() {
        //TODO fix - shadow is not generated before any changes (when program starts)
        double currentWidth = anchorPaneLeftGlobal.getWidth();
        anchorPaneLeftGlobal.setStyle(
                "-fx-background-color: linear-gradient(from " + currentWidth + "px 0px to " + (currentWidth-15) + "px 0px, lightgray, white)"
        );
    }

    //changing size of anchorPane (vista holder) in scrollPane
    private static void resizeAnchorPane() {
        if (currentVistaGlobal == null) return;
        currentVistaGlobal.setPrefWidth(scrollPaneGlobal.getViewportBounds().getWidth());
    }
}
