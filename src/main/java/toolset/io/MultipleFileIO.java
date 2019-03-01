package toolset.io;

import sections.Interruptible;
import sections.imagecopyfinder.GroupedFile;
import sections.imagecopyfinder.GroupedFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MultipleFileIO {

    public static List<GroupedFolder> openRecursiveFolders(List<GroupedFolder> groupedFolders, Interruptible interruptible) {
        var out = new ArrayList<GroupedFolder>();
        for (int i = 0; i < groupedFolders.size(); i++) {
            interruptible.reportProgress("Opening recursively (" + (i+1) + "/" + groupedFolders.size() + "): " + groupedFolders.get(i).getFolder().getName());
            GroupedFolder groupedFolder = groupedFolders.get(i);
            out.add(groupedFolder);
            if (!groupedFolder.isOpenRecursively()) continue;
            openRecursively(out, groupedFolder);
        }
        return out;
    }

    private static void openRecursively(List<GroupedFolder> groupedFolders, GroupedFolder template) {
        for (var file : Objects.requireNonNull(template.getFolder().listFiles())) {
            if (!file.isDirectory()) continue;
            System.out.println(file.getAbsolutePath());
            var groupedFolder = new GroupedFolder(template, file);
            groupedFolders.add(groupedFolder);
            openRecursively(groupedFolders, groupedFolder);
        }
    }

    public static List<GroupedFile> loadFilesFromFolders(List<GroupedFolder> groupedFolders) {
        var files = getFiles(groupedFolders);
        if (files.size() == 0) return Collections.emptyList();
        return files;
    }

    private static List<GroupedFile> getFiles(List<GroupedFolder> groupedFolders) {
        var groupedFiles = new ArrayList<GroupedFile>();
        for (var groupedFolder : groupedFolders) {
            File[] filesToAdd = groupedFolder.getFolder().listFiles();
            if (filesToAdd == null) continue;
            for (var file : filesToAdd) {
                groupedFiles.add(new GroupedFile(groupedFolder.getGroupId(), groupedFolder.getCompareGroup(), file));
            }
        }

        return groupedFiles;
    }

    public static List<GroupedFile> filterOutNonImages(List<GroupedFile> files) {
        return files
                .stream()
                .filter(MultipleFileIO::isImage)
                .collect(Collectors.toList());
    }

    private static boolean isImage(GroupedFile file) {
        if (file.getFile().isDirectory()) return false;
        String s = "";
        try {
            s = Files.probeContentType(file.getFile().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toLowerCase().contains("image");
    }



}
