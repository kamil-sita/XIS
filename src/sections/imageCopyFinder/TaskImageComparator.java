package sections.imageCopyFinder;

import javafx.concurrent.Task;

import java.io.File;

public class TaskImageComparator extends Task<ImageComparator> {

    private ImageComparator imageComparator;
    private String fileFolder;

    public TaskImageComparator(String fileFolder) {
        imageComparator = new ImageComparator();
        this.fileFolder = fileFolder;
    }

    public ImageComparator call() {
        File folder;
        try {
            folder = new File(fileFolder);
        } catch (Exception e) {
            imageComparator.setStatus(ImageComparator.ImageComparatorStatus.IO_ERROR);
            e.printStackTrace();
            return imageComparator;
        }

        if (!folder.isDirectory()) {
            imageComparator.setStatus(ImageComparator.ImageComparatorStatus.NOT_FOLDER);
        }

        boolean status = imageComparator.initialize(folder);

        if (!status) {
            imageComparator.setStatus(ImageComparator.ImageComparatorStatus.NO_IMAGES_IN_DIRECTORY);
            return imageComparator;
        }

        if (imageComparator.getImagePairs().size() == 0) {
            imageComparator.setStatus(ImageComparator.ImageComparatorStatus.NO_PAIRS);
        } else {
            imageComparator.setStatus(ImageComparator.ImageComparatorStatus.SUCCESFUL);
        }

        return imageComparator;
    }

    public ImageComparator getImageComparator() {
        return imageComparator;
    }
}
