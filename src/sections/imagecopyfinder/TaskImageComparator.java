package sections.imagecopyfinder;

import javafx.concurrent.Task;
import toolset.io.MultipleFileIO;

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

        var folders = MultipleFileIO.getFoldersFromStrings(fileFolders);

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
