package sections.main;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import sections.ModuleTemplate;
import sections.imageCopyFinder.ImageCopyFinder;
import sections.welcomePage.WelcomePage;


public class MainViewController {

    //static elements

    private static Runnable runnable;

    private static Label labelStatusGlobal;
    private static AnchorPane vistaHolderGlobal;
    private static AnchorPane currentVistaGlobal;
    private static ScrollPane scrollPaneGlobal;
    private static ModuleTemplate currentModule;
    private static ProgressBar progressBarGlobal;

    public static ImageCopyFinder imageCopyFinder;


    //JavaFX Elements

    @FXML
    private AnchorPane vistaHolder;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label labelStatus;

    @FXML
    private ProgressBar progressBar;

    //Actions methods

    @FXML
    void mainPress(ActionEvent event) {
        WelcomePage welcomePage = new WelcomePage();
        AnchorPane anchorPane = welcomePage.getUserInterface();
        changeVista(anchorPane);
        currentModule = welcomePage;
        setStatus("welcome page loaded");
    }

    @FXML
    void imageCopyFinderPress(ActionEvent event) {
        imageCopyFinder = new ImageCopyFinder();
        AnchorPane anchorPane = imageCopyFinder.getUserInterface();
        changeVista(anchorPane);
        currentModule = imageCopyFinder;
        setStatus("imageCopyFinder module loaded");
    }

    //Other methods

    /**
     * Function called after initialization (JavaFX function)
     */
    @FXML
    public void initialize() {
        labelStatusGlobal = labelStatus;
        vistaHolderGlobal = vistaHolder;
        scrollPaneGlobal = scrollPane;
        progressBarGlobal = progressBar;
        mainPress(null);
        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            MainViewController.onWindowSizeChange();
        });
        onWindowSizeChange();


    }

    /**
     * Changes vista (main AnchorPane where content is displayed)
     * @param anchorPane new vista
     */
    public static void changeVista(AnchorPane anchorPane) {
        currentVistaGlobal = anchorPane;
        vistaHolderGlobal.getChildren().setAll((Node) anchorPane);
        onWindowSizeChange();
    }

    /**
     * Sets status (Label) in bottom left label
     * @param text text of label
     */
    public static void setStatus(String text) {
        labelStatusGlobal.setText(text);
    }

    /**
     * Function called when Stage changes it's size
     */
    public static void onWindowSizeChange() {
        resizeAnchorPane();
    }

    /**
     * Reloads anchorPane from last used class implementing ModuleTemplate
     */
    public static void reloadView() {
        changeVista(currentModule.getUserInterface());
        onWindowSizeChange();
    }

    public static ProgressBar getProgressBar() {
        return progressBarGlobal;
    }

    public static void bindProgress(ReadOnlyDoubleProperty progress) {
        progressBarGlobal.progressProperty().unbind();
        progressBarGlobal.progressProperty().bind(progress);
    }

    public static void unBindProgress() {
        progressBarGlobal.progressProperty().unbind();
    }

    /**
     * Changes preferred width of vista (AnchorPane) so it scales properly with stage size
     */
    private static void resizeAnchorPane() {
        if (currentVistaGlobal == null) return;
        currentVistaGlobal.setPrefWidth(scrollPaneGlobal.getViewportBounds().getWidth());
    }
}
