package XIS.sections.imagecopyfinder;

import XIS.sections.Interruptible;
import XIS.toolset.io.BufferedImageIO;
import XIS.toolset.io.MultipleFileIO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ComparableImageIO {

    public static List<ComparableImage> loadFiles(List<GroupedFolder> groupedFolders, int generatedMiniatureSize, Interruptible interruptible) {
        interruptible.reportProgress("Finding files in folder");
        var images = new ArrayList<ComparableImage>();
        var groupedFiles = MultipleFileIO.loadFilesFromFolders(groupedFolders);

        if (groupedFiles.size() == 0) return Collections.emptyList();

        long time = System.nanoTime();

        try (var bufferedImageIo = new BufferedImageIO()){
            for (int i = 0; i < groupedFiles.size(); i++) {
                var groupedFile = groupedFiles.get(i);
                if (i >= 10) {
                    double dt = getApproximateTimeLeftFileLoading(i, time, groupedFiles.size() - i);
                    interruptible.reportProgress("Generating preview for file (" + (i+1) + "/" + groupedFiles.size() + "). Estimated time left for generating previews: " + ((int) (dt)) + " seconds.");
                } else {
                    interruptible.reportProgress("Generating preview for file (" + (i+1) + "/" + groupedFiles.size() + ")");
                }

                interruptible.reportProgress((1.0*i)/images.size());

                var optionalImage = bufferedImageIo.getImageWithFailsafe(groupedFile.getFile(), interruptible);
                if (optionalImage.isPresent()) {
                    ComparableImage comparableImage = new ComparableImage(groupedFile, optionalImage.get(), generatedMiniatureSize);
                    images.add(comparableImage);
                }

                if (interruptible.isInterrupted()) return Collections.emptyList();
            }
        }



        return images;
    }

    private static double getApproximateTimeLeftFileLoading(int currentIteration, long time, int left) {
        double dt = System.nanoTime() - time;
        dt = dt * (left) / (currentIteration);
        dt /= 1000000000;
        return dt;
    }

}
