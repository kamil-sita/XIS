package sections;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

/**
 * Abstract class of main vista in user interface.
 */
public abstract class Vista {

    private Pane pane = null;
    /**
     * defaultLocation should show location of .fxml file with interface to load
     */
    protected String defaultLocation = null;

    public Pane getUserInterface() {
        OneBackgroundJobManager.interruptCurrentJobIfPossible();
        return lazyLoad();
    }

    protected Pane lazyLoad() {
        if (pane != null) {
            return pane;
        }
        setInterface(defaultLocation);
        return pane;
    }

    protected void setInterface(String location) {
        URL url = Vista.class.getClassLoader().getResource(location);
        try {
            pane = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
