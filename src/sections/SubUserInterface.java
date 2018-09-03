package sections;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public abstract class SubUserInterface {

    private AnchorPane anchorPane = null;
    protected String defaultLocation = null;

    public AnchorPane getUserInterface() {
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
        URL url = SubUserInterface.class.getClassLoader().getResource(location);
        try {
            anchorPane = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
