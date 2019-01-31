package sections.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import sections.Notifier;
import sections.OneBackgroundJobManager;
import sections.Vista;
import sections.automatedfilter.AutomatedFilter;
import sections.defaultpage.WelcomePage;
import sections.highpassfilter.HighPassFilter;
import sections.imagecompetition.ImageCompetition;
import sections.imagecopyfinder.ImageCopyFinder;
import sections.scannertonote.ScannerToNote;

import java.util.ArrayList;

public final class MainViewController {

    //static elements
    private static Label labelStatusGlobal;
    private static AnchorPane vistaHolderGlobal;
    private static AnchorPane currentVistaGlobal;
    private static ScrollPane scrollPaneGlobal;
    private static Vista currentModule;
    private static ProgressBar progressBarGlobal;

    private static ArrayList<Notifier> notifiers = new ArrayList<>();

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
        changeVistaIfChanged(welcomePage);
        currentModule = welcomePage;
        setStatus("welcome page loaded");
    }

    @FXML
    void imageCopyFinderPress(ActionEvent event) {
        imageCopyFinder = new ImageCopyFinder();
        changeVistaIfChanged(imageCopyFinder);
        currentModule = imageCopyFinder;
        setStatus("imagecopyfinder module loaded");
    }

    @FXML
    void scannerToNotePress(ActionEvent event) {
        var scannerToNote = new ScannerToNote();
        changeVistaIfChanged(scannerToNote);
        currentModule = scannerToNote;
        setStatus("scanner-to-note module loaded");
    }

    @FXML
    void highPassFilterPress(ActionEvent event) {
        var highPassFilter = new HighPassFilter();
        changeVistaIfChanged(highPassFilter);
        currentModule = highPassFilter;
        setStatus("high-pass-filter module loaded");
    }

    @FXML
    void imagesCompetitionPress(ActionEvent event) {
        var imageCompetition = new ImageCompetition();
        changeVistaIfChanged(imageCompetition);
        currentModule = imageCompetition;
        setStatus("image-competition-module loaded");
    }

    @FXML
    void automatedFilteringPress(ActionEvent event) {
        var automatedFilter = new AutomatedFilter();
        changeVistaIfChanged(automatedFilter);
        currentModule = automatedFilter;
        setStatus("automatedFilter module loaded");
    }


    @FXML
    public void interruptPress(ActionEvent event) {
        OneBackgroundJobManager.interruptIfPossible();
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
     * @param newVista module with AnchorPane
     */
    public static void changeVistaIfChanged(Vista newVista) {
        if (lastVistaUI == null || lastVistaUI != newVista.getUserInterface()) {
            currentVistaGlobal = newVista.getUserInterface();
            vistaHolderGlobal.getChildren().setAll((Node) newVista.getUserInterface());
            onWindowSizeChange();
        }
    }
    private static AnchorPane lastVistaUI = null;

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
    private static void onWindowSizeChange() {
        resizeAnchorPane();
        for (Notifier notifier : notifiers) {
            notifier.notify(scrollPaneGlobal.getViewportBounds().getWidth(), scrollPaneGlobal.getViewportBounds().getHeight());
        }
    }

    /**
     * Notifies notifiers waiting for window size change.
     * If current vista was changed, loads it.
     */
    public static void refreshVista() {
        changeVistaIfChanged(currentModule);
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
        if (notifier == null) return;
        notifiers.remove(notifier);
    }
}
