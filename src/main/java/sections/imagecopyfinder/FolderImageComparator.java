package sections.imagecopyfinder;

import sections.Interruptible;
import toolset.io.MultipleFileIO;

import java.util.Arrays;
import java.util.List;

public final class FolderImageComparator {

    private ImageComparator imageComparator;
    private String[] input;
    public FolderImageComparator(String[] input, int miniatureSize) {
        imageComparator = new ImageComparator(miniatureSize);
        this.input = input;
    }

    public ImageComparator compare(Interruptible interruptible) {

        var folderList = Arrays.asList(input);
        removeDuplicateStrings(folderList);
        if (folderList.size() == 0) {
            interruptible.getUserFeedback().popup("No input given");
            return null;
        }
        var groupedFolders = GroupedFoldersParser.parse(input);
        if (groupedFolders == null || groupedFolders.size() == 0) {
            interruptible.getUserFeedback().popup("No folders found");
            return null;
        }

       var groupedFoldersOpened =  MultipleFileIO.openRecursiveFolders(groupedFolders);

        boolean status = imageComparator.run(groupedFoldersOpened, interruptible);
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
