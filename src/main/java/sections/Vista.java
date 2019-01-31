package sections;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

/**
 * Abstract class of main vista in user interface.
 */
public abstract class Vista {

    private AnchorPane anchorPane = null;
    /**
     * defaultLocation should show location of .fxml file with interface to load
     */
    protected String defaultLocation = null;

    public AnchorPane getUserInterface() {
        OneBackgroundJobManager.interruptIfPossible();
        return lazyLoad();
    }

    protected AnchorPane lazyLoad() {
        if (anchorPane != null) {
            return anchorPane;
        }
        setInterface(defaultLocation);
        return anchorPane;
    }

    protected void setInterface(String location) {
        URL url = Vista.class.getClassLoader().getResource(location);
        try {
            anchorPane = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
