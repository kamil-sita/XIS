package XIS.sections.imagecopyfinder;

import XIS.sections.Vista;

import java.nio.file.FileSystems;

public final class ImageCopyFinderModuleVista extends Vista {

    private static final String LOCATION_VIEW1 = "/XIS/sections/imagecopyfinder/view1settings/view1.fxml";
    private static final String LOCATION_VIEW2 = "/XIS/sections/imagecopyfinder/view2comparison/view2.fxml";
    private static ImageComparator imageComparator;
    private static String deleteDirectory;

    public ImageCopyFinderModuleVista() {
        this.defaultLocation = LOCATION_VIEW1;
    }

    public void setInterface(ImageCopyFinderViews view) {
        switch (view) {
            case mainView:
                load(LOCATION_VIEW1);
                break;
            case compareCopiedImagesView:
                load(LOCATION_VIEW2);
                break;
        }
    }

    public static ImageComparator getImageComparator() {
        return imageComparator;
    }

    public static void setImageComparator(ImageComparator imageComparator) {
        ImageCopyFinderModuleVista.imageComparator = imageComparator;
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
