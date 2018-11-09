package toolset;

import java.io.File;

public final class FileManagementTools {

    public static void moveFile(File file, String deleteDirectory) {
        if (!new File(deleteDirectory).exists()) {
            new File(deleteDirectory).mkdir();
        }
        String s = file.getName();
        String localDeleteDirectory = deleteDirectory;
        localDeleteDirectory += s;
        file.renameTo(new File(localDeleteDirectory));
    }
}
