package sections.main;

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
        changeVista(welcomePage);
        currentModule = welcomePage;
        setStatus("welcome page loaded");
    }

    @FXML
    void imageCopyFinderPress(ActionEvent event) {
        imageCopyFinder = new ImageCopyFinder();
        changeVista(imageCopyFinder);
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
     * @param mt module with AnchorPane
     */
    public static void changeVista(ModuleTemplate mt) {
        currentVistaGlobal = mt.getUserInterface();
        vistaHolderGlobal.getChildren().setAll((Node) mt);
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
        changeVista(currentModule);
        onWindowSizeChange();
    }

    public static ProgressBar getProgressBar() {
        return progressBarGlobal;
    }

    /**
     * Changes preferred width of vista (AnchorPane) so it scales properly with stage size
     */
    private static void resizeAnchorPane() {
        if (currentVistaGlobal == null) return;
        currentVistaGlobal.setPrefWidth(scrollPaneGlobal.getViewportBounds().getWidth());
    }
}
