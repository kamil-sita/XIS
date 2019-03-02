package toolset.io;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StringIO {
    public static void saveString(String s, File file) {
        try {
            PrintWriter out = new PrintWriter(file);
            out.println(s);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (Exception e) {
           //
        }
        return null;
    }
}
