package sections.welcomePage;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import sections.SubUserInterface;

import java.io.IOException;
import java.net.URL;

public class WelcomePage implements SubUserInterface {

    private static final String LOCATION = "sections/welcomePage/welcomePage.fxml";
    private static AnchorPane anchorPane;

    public AnchorPane getUserInterface() {
        if (anchorPane == null) {
            URL url = SubUserInterface.class.getClassLoader().getResource(LOCATION);
            try {
                anchorPane = FXMLLoader.load(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return anchorPane;
    }
}
