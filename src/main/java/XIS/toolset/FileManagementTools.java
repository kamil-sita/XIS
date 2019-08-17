package XIS.toolset;

import XIS.sections.Feedback;

import java.io.File;

public final class FileManagementTools {

    public static boolean moveFile(File file, String deleteDirectory, Feedback feedback) {
        if (!new File(deleteDirectory).exists()) {
            if (!new File(deleteDirectory).mkdir()) {
                feedback.report("Couldn't create directory: " + deleteDirectory, FileManagementTools.class);
                return false;
            }
        }
        String s = file.getName();
        String localDeleteDirectory = deleteDirectory;
        localDeleteDirectory += s;
        if (!file.renameTo(new File(localDeleteDirectory))) {
            feedback.report("Couldn't move file: " + file.getName() + " to " + localDeleteDirectory, FileManagementTools.class);
            return false;
        }

        return true;
    }
}
