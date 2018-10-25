package sections.xicf;

import javafx.concurrent.Task;
import sections.UserFeedback;

import java.io.File;

public final class TaskImageComparator extends Task<ImageComparator> {

    private ImageComparator imageComparator;
    private String[] fileFolders;
    private boolean geometricalMode;

    public TaskImageComparator(String[] fileFolders, int miniatureSize, boolean geometricalMode) {
        imageComparator = new ImageComparator(miniatureSize);
        this.fileFolders = fileFolders;
        this.geometricalMode = geometricalMode;
    }

    public ImageComparator call() {
        File[] folders = new File[fileFolders.length];

        for (int i = 0; i < fileFolders.length; i++) {
            String s = fileFolders[i];
            File folder;
            try {
                folder = new File(s);
            } catch (Exception e) {
                imageComparator.setStatus(ImageComparator.ImageComparatorStatus.IO_ERROR);
                UserFeedback.popup("IOException caused by: " + s);
                return imageComparator;
            }

            if (!folder.isDirectory()) {
                imageComparator.setStatus(ImageComparator.ImageComparatorStatus.NOT_FOLDER);
                UserFeedback.popup("Not a folder: " + s);
            }
            folders[i] = folder;
        }


        boolean status = imageComparator.initialize(folders, geometricalMode);

        if (!status) {
            imageComparator.setStatus(ImageComparator.ImageComparatorStatus.NO_IMAGES_IN_DIRECTORY);
            return imageComparator;
        }

        if (imageComparator.getImagePairs().size() == 0) {
            imageComparator.setStatus(ImageComparator.ImageComparatorStatus.NO_PAIRS);
        } else {
            imageComparator.setStatus(ImageComparator.ImageComparatorStatus.SUCCESSFUL);
        }

        return imageComparator;
    }
}
