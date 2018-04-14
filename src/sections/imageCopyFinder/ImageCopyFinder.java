package sections.imageCopyFinder;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import sections.ModuleTemplate;

import java.io.IOException;
import java.net.URL;

public class ImageCopyFinder implements ModuleTemplate {

    private static final String LOCATION_VIEW1 = "sections/imageCopyFinder/view1/view1.fxml";
    private static final String LOCATION_VIEW2 = "sections/imageCopyFinder/view2/view2.fxml";
    private static AnchorPane anchorPane;
    private static ImageComparator imageComparator;

    public AnchorPane getUserInterface() {
        if (anchorPane == null) {
            setInterface(LOCATION_VIEW1);
        }
        return anchorPane;
    }

    private void setInterface(String location) {
        URL url = ModuleTemplate.class.getClassLoader().getResource(location);
        try {
            anchorPane = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInterface(ImageCopyFinderViews view) {
        switch (view) {
            case mainView:
                setInterface(LOCATION_VIEW1);
                break;
            case compareCopiedImagesView:
                setInterface(LOCATION_VIEW2);
                break;
        }
    }

    public static ImageComparator getImageComparator() {
        return imageComparator;
    }

    public static void setImageComparator(ImageComparator imageComparator) {
        ImageCopyFinder.imageComparator = imageComparator;
    }

    public enum ImageCopyFinderViews {
        mainView, compareCopiedImagesView
    }
}
