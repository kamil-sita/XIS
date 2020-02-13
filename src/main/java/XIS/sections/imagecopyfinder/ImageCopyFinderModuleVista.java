package XIS.sections.imagecopyfinder;

import XIS.sections.Vista;

import java.nio.file.FileSystems;
import java.util.List;

public final class ImageCopyFinderModuleVista extends Vista {

    private static final String LOCATION_VIEW1 = "/XIS/sections/imagecopyfinder/view1settings/view1.fxml";
    private static final String LOCATION_VIEW2 = "/XIS/sections/imagecopyfinder/view2comparison/view2.fxml";
    private static List<ComparedImagePair> imagePairList;
    private static String deleteDirectory;
    private static int imageSize;
    private static String inputString;

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

    public static List<ComparedImagePair> getImagePairList() {
        return imagePairList;
    }

    public static void setImagePairList(List<ComparedImagePair> imagePairList) {
        ImageCopyFinderModuleVista.imagePairList = imagePairList;
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

    public static int getImageSize() {
        return imageSize;
    }

    public static void setImageSize(int imageSize) {
        ImageCopyFinderModuleVista.imageSize = imageSize;
    }

    public static String getInputString() {
        return inputString;
    }

    public static void setInputString(String inputString) {
        ImageCopyFinderModuleVista.inputString = inputString;
    }

    public enum ImageCopyFinderViews {
        mainView, compareCopiedImagesView
    }
}
