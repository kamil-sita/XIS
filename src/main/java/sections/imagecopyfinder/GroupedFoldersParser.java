package sections.imagecopyfinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static sections.imagecopyfinder.CompareGroup.*;

public class GroupedFoldersParser {
    public static List<GroupedFolder> parse(String[] input) {
        ArrayList<GroupedFolder> groupedFolders = new ArrayList<>();
        int currentGroupdId = -1;
        boolean groupOpened = false;
        CompareGroup compareGroupForCurrentGroup = notDefinied;

        int lineId = -1;
        for (var line : input) {
            lineId++;
            if (!groupOpened) currentGroupdId++;

            //check if empty etc
            if (line == null || line.isEmpty()) continue;
            var lineTrimmed = line.trim(); //can be changed to .strip() when building with jdk 11
            if (lineTrimmed.isEmpty()) continue;

            //check if opening, handle it
            {
                boolean groupOpening = isOpening(lineTrimmed);
                if (groupOpening && groupOpened) handleError(lineId, "opening group when one is already opened", line);
                if (groupOpening) {
                    groupOpened = true;
                    compareGroupForCurrentGroup = getCompareGroupGroups(lineTrimmed);
                    if (compareGroupForCurrentGroup == notDefinied) handleError(lineId, "group for join not specified", line);
                    continue;
                }
            }

            //check if closing, handle it
            {
                boolean groupClosing = isClosing(lineTrimmed);
                if (groupClosing && !groupOpened) handleError(lineId, "closing group when there is no group opened", line);
                if (groupClosing) {
                    groupOpened = false;
                    continue;
                }
            }

            //if in group, handle it
            {
                if (groupOpened) {
                    CompareGroup compareGroup = getCompareGroupFolders(lineTrimmed);
                    if (compareGroup != all) handleError(lineId, "group compare argument inside join", line);
                    boolean isOpenRecursively = isOpenRecursively(lineTrimmed);
                    var finalLine = stripFromOpenRecursively(lineTrimmed);
                    File f = getFolder(finalLine);
                    if (f == null) handleError(lineId, "failed to open file", line);
                    var groupedFolder = new GroupedFolder(currentGroupdId, compareGroupForCurrentGroup, isOpenRecursively, f);
                    groupedFolders.add(groupedFolder);
                    continue;
                }
            }

            //not in group

            CompareGroup compareGroup = getCompareGroupFolders(lineTrimmed);
            boolean isOpenRecursively = isOpenRecursively(lineTrimmed);
            var finalLine = stripFromOpenRecursively(lineTrimmed);
            File f = getFolder(finalLine);
            if (f == null) handleError(lineId, "failed to open file", line);
            var groupedFolder = new GroupedFolder(currentGroupdId, compareGroup, isOpenRecursively, f);
            groupedFolders.add(groupedFolder);
        }
        return groupedFolders;
    }

    private static boolean isOpening(String s) {
        var c = s.toLowerCase();
        if (c.matches("[lg]\\s+join\\s*\\(")) return true; //original regex: [lg]\s+join\s*\(
        //should match sequences like:
        //l join (
        //g     join (
        //l join(
        if (c.matches("join\\s*\\(")) return true; //original regex: join\s*\(
        //should match sequences like:
        //join     (
        //join (
        //join(
        return false;
    }

    private static boolean isClosing(String s) {
        return s.equals(")");
    }

    //should be called after stripping from group argument
    private static boolean isOpenRecursively(String s) {
        return s.trim().startsWith("+");
    }

    private static String stripFromOpenRecursively(String s) {
        if (s.trim().startsWith("+")) {
            return s.trim().substring(1).trim();
        }
        return s.trim();
    }

    private static CompareGroup getCompareGroupGroups(String s) {
        var c = s.toLowerCase().charAt(0);
        switch (c) {
            case 'g':
                return globalOnly;
            case 'l':
                return localOnly;
            case 'j':
                return all;
        }
        return notDefinied;
    }

    private static CompareGroup getCompareGroupFolders(String s) {
        var c = s.toLowerCase();
        if (c.startsWith("g ")) {
            return globalOnly;
        } else if (c.startsWith("l ")) {
            return localOnly;
        } else {
            return all;
        }
    }

    private static String stripLineFromCompareArgument(String s) {
        String outputString;
        var lowerCase = s.toLowerCase();
        if (lowerCase.startsWith("g ")) {
            outputString = s.substring(2);
        } else if (lowerCase.startsWith("l ")) {
            outputString = s.substring(2);
        } else {
            return s;
        }
        return outputString.trim();
    }

    private static File getFolder(String s) {
        File folder = null;
        try {
            folder = new File(s);
        } catch (Exception e) {
            //userFeedback.popup("Exception caused by: " + s);
            return null;
        }
        if (folder == null) {
            //userFeedback.popup("Not a file/directory: " + s);
            return null;
        }
        if (!folder.isDirectory()) {
            return null;
                //userFeedback.popup("Not a folder: " + s);
        }
        return folder;
    }

    private static void handleError(int lineId, String cause, String line) {
        throw new ParsingException(cause, line, lineId);
    }
}
