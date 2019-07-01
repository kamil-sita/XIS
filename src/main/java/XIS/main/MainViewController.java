package XIS.main;

import XIS.sections.Notifier;
import XIS.sections.SingleJobManager;
import XIS.sections.Vista;
import XIS.sections.XisController;
import XIS.sections.automatedfilter.AutomatedFilterModuleVista;
import XIS.sections.compression.CompressionModuleVista;
import XIS.sections.defaultpage.WelcomePageModuleVista;
import XIS.sections.imagecopyfinder.ImageCopyFinderModuleVista;
import XIS.sections.scanprocessing.ScanProcessingModuleVista;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MainViewController {

    private Vista currentModule;
    private static MainViewController staticController;

    private final List<Notifier> notifiers = Collections.synchronizedList(new ArrayList<>());
    private boolean appClosed = false;

    //JavaFX Elements

    @FXML
    private AnchorPane vistaParent;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label labelStatus;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ColumnConstraints gridPaneBarLeft;

    @FXML
    private ColumnConstraints gridPaneBarRight;

    private ImageCopyFinderModuleVista imageCopyFinder;


    //Actions methods

    @FXML
    void mainPress(ActionEvent event) {
        WelcomePageModuleVista welcomePageModuleVista = new WelcomePageModuleVista();
        changeVistaIfChanged(welcomePageModuleVista);
        currentModule = welcomePageModuleVista;
        setStatus("welcome page loaded");
    }

    @FXML
    void imageCopyFinderPress(ActionEvent event) {
        imageCopyFinder = new ImageCopyFinderModuleVista();
        changeVistaIfChanged(imageCopyFinder);
        currentModule = imageCopyFinder;
        setStatus("imagecopyfinder module loaded");
    }

    @FXML
    void automatedFilteringPress(ActionEvent event) {
        var automatedFilter = new AutomatedFilterModuleVista();
        changeVistaIfChanged(automatedFilter);
        currentModule = automatedFilter;
        setStatus("automatedFilter module loaded");
    }

    @FXML
    void imageCompressionPress(ActionEvent event) {
        var imageCompression = new CompressionModuleVista();
        changeVistaIfChanged(imageCompression);
        currentModule = imageCompression;
        setStatus("image compression module loaded");
    }

    @FXML
    void scanProcessingPress(ActionEvent event) {
        var scanProcessing = new ScanProcessingModuleVista();
        changeVistaIfChanged(scanProcessing);
        currentModule = scanProcessing;
        setStatus("scan processing module loaded");
    }


    @FXML
    public void interruptPress(ActionEvent event) {
        SingleJobManager.attemptInterrupt();
    }


    //Other methods

    /**
     * Function called after initialization (JavaFX function)
     */
    @FXML
    public void initialize() {
        staticController = this;
        mainPress(null);
        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            onWindowSizeChange();
        });
        onWindowSizeChange();
    }

    /**
     * Changes vista (XIS.main AnchorPane where content is displayed)
     * @param newVista module with AnchorPane
     */
    public void changeVistaIfChanged(Vista newVista) {
        Platform.runLater(() -> {
            if (currentVista == null || currentVista != newVista.getUserInterface()) {
                if (currentController != null) {
                    SingleJobManager.attemptInterrupt();
                    currentController.deregisterAllNotifiers();
                }
                currentController = newVista.getController();
                currentVista = newVista.getUserInterface();
                vistaParent.getChildren().setAll((Node) newVista.getUserInterface());
                onWindowSizeChange();
            }
        });
    }
    private Pane currentVista = null;
    private XisController currentController = null;

    /**
     * Sets status (Label) in bottom left label
     * @param text text of label
     */
    public void setStatus(String text) {
        labelStatus.setText(text);
    }

    /**
     * Function called when Stage changes it's size
     */
    private void onWindowSizeChange() {
        resizeAnchorPane();
        synchronized (notifiers) {
            for (int i = 0; i < notifiers.size(); i++) { //can't be in for each loop as notifiers might be changed in another thread
                Notifier notifier = notifiers.get(i);
                notifier.notify(scrollPane.getViewportBounds().getWidth(), scrollPane.getViewportBounds().getHeight());
            }
        }
    }

    /**
     * Notifies notifiers waiting for window size change.
     * If current vista was changed, loads it.
     */
    public void refreshVista() {
        changeVistaIfChanged(currentModule);
        onWindowSizeChange();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }


    /**
     * Changes preferred width of vista (AnchorPane) so it scales properly with stage size
     */
    private void resizeAnchorPane() {
        if (currentVista == null) return;
        hideBarsIfLackSpace();
        currentVista.setPrefWidth(scrollPane.getViewportBounds().getWidth());
    }

    private static boolean targetSmall = false;
    private static final int SMALL_THRESHOLD = 800;
    private static final int BIG_THRESHOLD = 1000;

    private void hideBarsIfLackSpace() {
        if (scrollPane.getViewportBounds().getWidth() <= SMALL_THRESHOLD) {
            targetSmall = true;
        } else if (scrollPane.getViewportBounds().getWidth() >= BIG_THRESHOLD) {
            targetSmall = false;
        }
        if (targetSmall) {
            staticController.gridPaneBarLeft.setHgrow(Priority.NEVER);
            staticController.gridPaneBarLeft.setPrefWidth(0);
            staticController.gridPaneBarLeft.setMinWidth(0);
            staticController.gridPaneBarLeft.setMaxWidth(0);
            staticController.gridPaneBarRight.setHgrow(Priority.NEVER);
            staticController.gridPaneBarRight.setPrefWidth(0);
            staticController.gridPaneBarRight.setMinWidth(0);
            staticController.gridPaneBarRight.setMaxWidth(0);
        } else {
            staticController.gridPaneBarLeft.setHgrow(Priority.SOMETIMES);
            staticController.gridPaneBarLeft.setPrefWidth(100);
            staticController.gridPaneBarLeft.setMinWidth(0);
            staticController.gridPaneBarLeft.setMaxWidth(10000);
            staticController.gridPaneBarRight.setHgrow(Priority.SOMETIMES);
            staticController.gridPaneBarRight.setPrefWidth(100);
            staticController.gridPaneBarRight.setMinWidth(0);
            staticController.gridPaneBarRight.setMaxWidth(10000);
        }
    }

    public void addNotifier(Notifier notifier) {
        notifiers.add(notifier);
    }

    public void removeNotifier(Notifier notifier) {
        if (notifier == null) return;
        notifiers.remove(notifier);
    }

    public void removeAll(List<Notifier> notifiersToRemove) {
        notifiers.removeAll(notifiersToRemove);
    }

    public static MainViewController getInstance() {
        return staticController;
    }

    public ImageCopyFinderModuleVista getImageCopyFinder() {
        return imageCopyFinder;
    }

    public boolean isAppClosed() {
        return appClosed;
    }

    public void setAppClosed(boolean appClosed) {
        this.appClosed = appClosed;
    }
}
