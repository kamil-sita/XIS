package XIS.sections.imagecopyfinder;

import XIS.sections.Interruptible;
import XIS.toolset.io.MultipleFileIO;

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
        if (folderList.size() == 0) {
            interruptible.getUserFeedback().popup("No input given");
            return null;
        }
        List<GroupedFolder> groupedFolders = null;
        try {
            groupedFolders = GroupedFoldersParser.parse(input);
        } catch (Exception e) {
            interruptible.getUserFeedback().popup(e.getMessage());
            imageComparator.setStatus(ImageComparator.ImageComparatorStatus.ERROR_ALREADY_HANDLED);
            return null;
        }
        if (groupedFolders == null || groupedFolders.size() == 0) {
            interruptible.getUserFeedback().popup("No folders found");
            imageComparator.setStatus(ImageComparator.ImageComparatorStatus.ERROR_ALREADY_HANDLED);
            return null;
        }

       var groupedFoldersOpened =  MultipleFileIO.openRecursiveFolders(groupedFolders, interruptible);

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
}
