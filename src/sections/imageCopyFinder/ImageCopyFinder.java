package sections.imageCopyFinder;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import sections.ModuleTemplate;

import java.io.IOException;
import java.net.URL;

public class ImageCopyFinder implements ModuleTemplate {

    private static final String LOCATION = "sections/imageCopyFinder/view1/view1.fxml";
    private static AnchorPane anchorPane;

    public AnchorPane getUserInterface() {
        if (anchorPane == null) {
            URL url = ModuleTemplate.class.getClassLoader().getResource(LOCATION);
            try {
                anchorPane = FXMLLoader.load(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return anchorPane;
    }
}
