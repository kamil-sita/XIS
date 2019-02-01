package toolset;

import sections.UserFeedback;

import java.io.File;

public final class FileManagementTools {

    public static void moveFile(File file, String deleteDirectory) {
        if (!new File(deleteDirectory).exists()) {
            if (!new File(deleteDirectory).mkdir()) {
                UserFeedback.popup("Couldn't create directory");
                return;
            }
        }
        String s = file.getName();
        String localDeleteDirectory = deleteDirectory;
        localDeleteDirectory += s;
        if (!file.renameTo(new File(localDeleteDirectory))) {
            UserFeedback.popup("Couldn't move file!");
        }
        try {
            System.out.println(file.getCanonicalPath());
            System.out.println(file.getName());
            System.out.println("+++++");
        } catch (Exception e) {
            //
        }
    }
}
