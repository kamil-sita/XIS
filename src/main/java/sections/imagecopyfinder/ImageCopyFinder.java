package sections.imagecopyfinder;

import sections.Vista;

import java.nio.file.FileSystems;

public final class ImageCopyFinder extends Vista {

    private static final String LOCATION_VIEW1 = "sections/imagecopyfinder/view1settings/view1.fxml";
    private static final String LOCATION_VIEW2 = "sections/imagecopyfinder/view2comparison/view2.fxml";
    private static ImageComparator imageComparator;
    private static String deleteDirectory;

    public ImageCopyFinder() {
        this.defaultLocation = LOCATION_VIEW1;
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

    public static String getDeleteDirectory() {
        return deleteDirectory;
    }

    public static void setDeleteDirectory(String loc) {
        String separator = FileSystems.getDefault().getSeparator();
        if (!loc.endsWith(separator)) {
            loc = loc + separator;
        }
        deleteDirectory = loc;
    }

    public enum ImageCopyFinderViews {
        mainView, compareCopiedImagesView
    }
}
