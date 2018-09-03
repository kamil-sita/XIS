package sections.imageCopyFinder.imageInfoView;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import sections.SubUserInterface;

import java.io.IOException;


//I tried to refactor this class, but is seems that changing the way controller is obtained results in nullpointerexception
//not sure why, will try again some time
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
