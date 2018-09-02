package sections.imageCopyFinder.imageInfoView;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import sections.SubUserInterface;

import java.io.IOException;
import java.net.URL;

public final class ImageInfoView extends SubUserInterface {
    private static final String LOCATION = "imageInfoView.fxml";
    private AnchorPane anchorPane;
    private ImageInfoViewController imageInfoViewController;
    private boolean loaded = false;

    public AnchorPane getUserInterface() {
        load();
        return anchorPane;
    }

    public ImageInfoViewController getController() {
        load();
        return imageInfoViewController;
    }

    private void load() {
        if (loaded) return;
        URL url = SubUserInterface.class.getClassLoader().getResource(LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            anchorPane = fxmlLoader.load(getClass().getResource(LOCATION).openStream());
            imageInfoViewController = (ImageInfoViewController) fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loaded = true;
    }
}
