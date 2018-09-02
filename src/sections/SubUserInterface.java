package sections;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public abstract class SubUserInterface {

    private AnchorPane anchorPane = null;
    protected String defaultLocation = null;

    /**
     *
     * @return AnchorPane that is this user interface
     */
    public AnchorPane getUserInterface() {
        return lazyLoadAnchorPane();
    }

    protected AnchorPane lazyLoadAnchorPane() {
        if (anchorPane != null) {
            return anchorPane;
        }
        URL url = SubUserInterface.class.getClassLoader().getResource(defaultLocation);
        try {
            anchorPane = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return anchorPane;
    }
}
