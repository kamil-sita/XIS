package toolset.io;

import sections.UserFeedback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleFileIO {

    public static File[] getFoldersFromStrings(String[] fileFolders) {
        File[] folders = new File[fileFolders.length];
        for (int i = 0; i < fileFolders.length; i++) {
            String s = fileFolders[i];
            File folder = null;
            try {
                folder = new File(s);
            } catch (Exception e) {
                UserFeedback.popup("Exception caused by: " + s);
            }
            if (!folder.isDirectory()) {
                UserFeedback.popup("Not a folder: " + s);
            }
            folders[i] = folder;
        }
        return folders;
    }

    public static List<File> loadFilesFromFolders(File[] folders) {
        ArrayList<File> files = getFiles(folders);
        if (files.size() == 0) return Collections.emptyList();
        return files;
    }

    private static ArrayList<File> getFiles(File[] folders) {
        ArrayList<File> files = new ArrayList<>();
        for (int i = 0; i < folders.length; i++) {
            File[] filesToAdd = folders[i].listFiles();
            files.addAll(Arrays.asList(filesToAdd));
        }
        return files;
    }

    public static List<File> filterOutNonImages(List<File> files) {
        return files
                .stream()
                .filter(MultipleFileIO::isImage)
                .collect(Collectors.toList());
    }

    private static boolean isImage(File file) {
        return true;
    }



}
