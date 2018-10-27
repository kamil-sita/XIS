package sections.imagecopyfinder;

import sections.UserFeedback;
import toolset.imagetools.BufferedImageIO;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ComparableImagesIO {

    public static Optional<List<ComparableImage>> loadFiles(File[] folders, int generatedMiniatureSize) {
        UserFeedback.reportProgress("Finding files in folder");
        var images = new ArrayList<ComparableImage>();
        ArrayList<File> files = getFiles(folders);

        if (files.size() == 0) return Optional.empty();

        int i = 0;

        long time = System.nanoTime();

        for (File file : files) {
            if (i >= 10) {
                double dt = getApproximateTimeLeftFileLoading(i, time, files.size() - i);
                UserFeedback.reportProgress("Generating preview for file (" + (i+1) + "/" + files.size() + "). Estimated time left for generating previews: " + ((int) (dt)) + " seconds.");
            } else {
                UserFeedback.reportProgress("Generating preview for file (" + (i+1) + "/" + files.size() + ")");
            }

            UserFeedback.reportProgress(1.0*i/images.size());

            i++;
            var optionalImage = BufferedImageIO.getImage(file);
            if (optionalImage.isPresent()) {
                ComparableImage comparableImage = new ComparableImage(file, optionalImage.get());
                comparableImage.generateData(generatedMiniatureSize);
                optionalImage = null; //so it's maybe easier for the garbage collector
                images.add(comparableImage);
            }

        }

        return Optional.of(images);
    }

    private static ArrayList<File> getFiles(File[] folders) {
        ArrayList<File> files = new ArrayList<>();
        for (int i = 0; i < folders.length; i++) {
            File[] filesToAdd = folders[i].listFiles();
            files.addAll(Arrays.asList(filesToAdd));
        }
        return files;
    }

    private static double getApproximateTimeLeftFileLoading(int i, long time, int i2) {
        //calculating estimated time left
        double dt = System.nanoTime() - time;
        dt = dt * (i2) / (i);
        dt /= 1000000000;
        return dt;
    }

}
