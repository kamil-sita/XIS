package XIS.sections;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

/**
 * Abstract class of XIS.main vista in user interface.
 */
public abstract class Vista {

    private Pane pane = null;
    private XisController xisController = null;
    /**
     * defaultLocation should show location of .fxml file with interface to load
     */
    protected String defaultLocation = null;

    public Pane getUserInterface() {
        lazyLoad();
        return pane;
    }

    public XisController getController() {
        return xisController;
    }

    protected void lazyLoad() {
        if (pane != null) {
            return;
        }
        load(defaultLocation);
    }

    protected void load(String location) {
        URL url = Vista.class.getClassLoader().getResource(location);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            pane = fxmlLoader.load(url.openStream());
            xisController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
