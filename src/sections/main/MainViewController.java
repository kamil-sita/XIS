package sections.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import sections.Notifier;
import sections.SubUserInterface;
import sections.highpassfilter.HighPassFilter;
import sections.imagecopyfinder.ImageCopyFinder;
import sections.scannertonote.ScannerToNote;
import sections.defaultpage.WelcomePage;

import java.util.ArrayList;


public final class MainViewController {

    private static MainViewController mainViewController;

    //static elements
    private static Label labelStatusGlobal;
    private static AnchorPane vistaHolderGlobal;
    private static AnchorPane currentVistaGlobal;
    private static ScrollPane scrollPaneGlobal;
    private static SubUserInterface currentModule;
    private static ProgressBar progressBarGlobal;

    private static ArrayList<Notifier> notifiers = new ArrayList<>();

    public static ImageCopyFinder imageCopyFinder;


    //JavaFX Elements

    @FXML
    private GridPane gridPane;

    @FXML
    private TilePane tilePane;

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
        setStatus("imagecopyfinder module loaded");
    }

    @FXML
    void scannerToNotePress(ActionEvent event) {
        var scannerToNote = new ScannerToNote();
        changeVista(scannerToNote);
        currentModule = scannerToNote;
        setStatus("scanner to note module loaded");
    }

    @FXML
    void highPassFilterPress(ActionEvent event) {
        var highPassFilter = new HighPassFilter();
        changeVista(highPassFilter);
        currentModule = highPassFilter;
        setStatus("high pass filter module loaded");
    }

    //Other methods

    /**
     * Function called after initialization (JavaFX function)
     */
    @FXML
    public void initialize() {
        mainViewController = this;
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
    public static void changeVista(SubUserInterface mt) {
        currentVistaGlobal = mt.getUserInterface();
        vistaHolderGlobal.getChildren().setAll((Node) mt.getUserInterface());
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
        for (Notifier notifier : notifiers) {
            notifier.notify(scrollPaneGlobal.getViewportBounds().getWidth(), scrollPaneGlobal.getViewportBounds().getHeight());
        }
    }

    /**
     * Reloads anchorPane from last used class implementing SubUserInterface
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

    public static void addNotifier(Notifier notifier) {
        notifiers.add(notifier);
    }

    public static void removeNotifier(Notifier notifier) {
        notifiers.remove(notifier);
    }
}
