package toolset.io;

import sections.UserFeedback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleFileIO {

    public static File[] getFoldersFromStrings(List<String> fileFolders, UserFeedback userFeedback) {
        File[] folders = new File[fileFolders.size()];
        for (int i = 0; i < fileFolders.size(); i++) {
            String s = fileFolders.get(i);
            File folder = null;
            try {
                folder = new File(s);
            } catch (Exception e) {
                userFeedback.popup("Exception caused by: " + s);
            }
            if (folder == null) {
                userFeedback.popup("Not a file/directory: " + s);
                return null;
            }
            if (!folder.isDirectory()) {
                userFeedback.popup("Not a folder: " + s);
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
        for (File folder : folders) {
            File[] filesToAdd = folder.listFiles();
            if (filesToAdd == null) continue;
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
        if (file.isDirectory()) return false;
        String s = "";
        try {
            s = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toLowerCase().contains("image");
    }



}
