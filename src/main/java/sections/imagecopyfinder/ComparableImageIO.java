package sections.imagecopyfinder;

import sections.Interruptible;
import toolset.io.BufferedImageIO;
import toolset.io.MultipleFileIO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparableImageIO {

    public static List<ComparableImage> loadFiles(File[] folders, int generatedMiniatureSize, Interruptible interruptible) {
        interruptible.reportProgress("Finding files in folder");
        var images = new ArrayList<ComparableImage>();
        List<File> files = MultipleFileIO.loadFilesFromFolders(folders);

        if (files.size() == 0) return Collections.emptyList();

        long time = System.nanoTime();

        for (int i = 0; i < files.size(); i++) {
            var file = files.get(i);
            if (i >= 10) {
                double dt = getApproximateTimeLeftFileLoading(i, time, files.size() - i);
                interruptible.reportProgress("Generating preview for file (" + (i+1) + "/" + files.size() + "). Estimated time left for generating previews: " + ((int) (dt)) + " seconds.");
            } else {
                interruptible.reportProgress("Generating preview for file (" + (i+1) + "/" + files.size() + ")");
            }

            interruptible.reportProgress((1.0*i)/images.size());

            var optionalImage = BufferedImageIO.getImageWithFailsafe(file, interruptible);
            if (optionalImage.isPresent()) {
                ComparableImage comparableImage = new ComparableImage(file, optionalImage.get(), generatedMiniatureSize);
                images.add(comparableImage);
            }

            if (interruptible.isInterrupted()) return Collections.emptyList();
        }

        return images;
    }

    private static double getApproximateTimeLeftFileLoading(int i, long time, int i2) {
        double dt = System.nanoTime() - time;
        dt = dt * (i2) / (i);
        dt /= 1000000000;
        return dt;
    }

}
