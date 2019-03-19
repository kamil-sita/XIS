package XIS.sections.imagecopyfinder.imageinfoview;

import XIS.sections.Vista;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public final class ImageInfoView extends Vista {
    private static final String LOCATION = "imageInfoView.fxml";
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
