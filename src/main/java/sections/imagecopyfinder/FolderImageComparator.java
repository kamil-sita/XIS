package sections.imagecopyfinder;

import sections.Interruptible;
import sections.UserFeedback;
import toolset.io.MultipleFileIO;

import java.util.Arrays;
import java.util.List;

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

        var folderList = Arrays.asList(fileFolders);
        removeDuplicateStrings(folderList);
        if (folderList.size() == 0) {
            UserFeedback.popup("No input given");
            return null;
        }
        var folders = MultipleFileIO.getFoldersFromStrings(folderList);
        if (folders == null || folders.length == 0) {
            UserFeedback.popup("No folders found");
            return null;
        }


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

    private static void removeDuplicateStrings(List<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            final int[] occurrenceCount = {0};
            var stringToCheckFor = strings.get(i);
            strings.removeIf(s -> {
                if (s.equals(stringToCheckFor)) {
                    occurrenceCount[0]++;
                    return occurrenceCount[0] != 1;
                }
                return false;
            });
        }
    }
}
