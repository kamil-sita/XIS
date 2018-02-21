package sections.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import sections.imageCopyFinder.ImageCopyFinder;

public class ControllerMV {

    public static Label labelStatusGlobal;
    public static AnchorPane vistaHolderGlobal;
    public static ScrollPane scrollPaneGlobal;

    public static AnchorPane anchorPaneLeftGlobal;


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
        vistaHolderGlobal.getChildren().setAll((Node) anchorPane);
    }

    public static void setStatus(String text) {
        labelStatusGlobal.setText(text);
    }

    public static void windowSizeChange() {
        generateShadow();
        resizeAnchorPane();
    }

    private static void generateShadow() {
        double currentWidth = anchorPaneLeftGlobal.getWidth();
        System.out.println(currentWidth);
        anchorPaneLeftGlobal.setStyle(
                "-fx-background-color: linear-gradient(from " + currentWidth + "px 0px to " + (currentWidth-25) + "px 0px, lightgray, white)"
        );
    }

    //changing size of anchorPane (vista holder) in scrollPane
    private static void resizeAnchorPane() {
        //TODO not working yet
        vistaHolderGlobal.setPrefWidth(scrollPaneGlobal.getWidth() - scrollPaneGlobal.getViewportBounds().getWidth());
    }
}
