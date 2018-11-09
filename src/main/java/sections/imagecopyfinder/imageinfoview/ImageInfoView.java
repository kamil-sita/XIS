package sections.imagecopyfinder.imageinfoview;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import sections.SubUserInterface;

import java.io.IOException;

public final class ImageInfoView extends SubUserInterface {
    private static final String LOCATION = "imageinfoview.fxml";
    private AnchorPane anchorPane;
    private ImageInfoViewController imageInfoViewController;

    public ImageInfoView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            anchorPane = fxmlLoader.load(getClass().getResource(LOCATION).openStream());
            imageInfoViewController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AnchorPane getUserInterface() {
        return anchorPane;
    }

    public ImageInfoViewController getController() {
        return imageInfoViewController;
    }

}
