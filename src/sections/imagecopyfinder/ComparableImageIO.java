package sections.imagecopyfinder;

import sections.UserFeedback;
import toolset.imagetools.BufferedImageIO;
import toolset.io.MultipleFileIO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparableImageIO {

    public static List<ComparableImage> loadFiles(File[] folders, int generatedMiniatureSize) {
        UserFeedback.reportProgress("Finding files in folder");
        var images = new ArrayList<ComparableImage>();
        List<File> files = MultipleFileIO.loadFilesFromFolders(folders);

        if (files.size() == 0) return Collections.emptyList();

        long time = System.nanoTime();

        for (int i = 0; i < files.size(); i++) {
            var file = files.get(i);
            System.out.println(file.getName());
            if (i >= 10) {
                double dt = getApproximateTimeLeftFileLoading(i, time, files.size() - i);
                UserFeedback.reportProgress("Generating preview for file (" + (i+1) + "/" + files.size() + "). Estimated time left for generating previews: " + ((int) (dt)) + " seconds.");
            } else {
                UserFeedback.reportProgress("Generating preview for file (" + (i+1) + "/" + files.size() + ")");
            }

            UserFeedback.reportProgress((1.0*i)/images.size());

            i++;
            var optionalImage = BufferedImageIO.getImage(file);
            if (optionalImage.isPresent()) {
                ComparableImage comparableImage = new ComparableImage(file, optionalImage.get(), generatedMiniatureSize);
                optionalImage = null; //so it's maybe easier for the garbage collector
                images.add(comparableImage);
            }

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
