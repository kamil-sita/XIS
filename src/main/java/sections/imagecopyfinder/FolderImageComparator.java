package sections.imagecopyfinder;

import sections.Interruptible;
import toolset.io.MultipleFileIO;

public final class FolderImageComparator {

    private ImageComparator imageComparator;
    private String[] fileFolders;
    private boolean geometricalMode;

    public FolderImageComparator(String[] fileFolders, int miniatureSize, boolean geometricalMode) {
        imageComparator = new ImageComparator(miniatureSize);
        this.fileFolders = fileFolders;
        this.geometricalMode = geometricalMode;
    }

    public ImageComparator compare(Interruptible interruptible) {

        var folders = MultipleFileIO.getFoldersFromStrings(fileFolders);

        boolean status = imageComparator.initialize(folders, geometricalMode, interruptible);
        if (interruptible.isInterrupted()) return null;

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
