package sections.imageCopyFinder;

import universal.tools.BufferedImageTools.BufferedImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ImageComparator {

    private ArrayList<ComparableImage> images;
    boolean initialized = false;

    public boolean initialize (File folder) {
        File[] files = folder.listFiles();
        if (files.length == 0) return false;

        for (File file : files) {
            BufferedImage image = BufferedImageIO.getImage(file);
            if (image != null) {
                ComparableImage comparableImage = new ComparableImage(file, image);
                images.add(comparableImage);
            }
        }
        return images.size() > 0;
    }
}
