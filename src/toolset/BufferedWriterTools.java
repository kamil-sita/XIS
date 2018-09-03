package toolset;

import java.io.BufferedWriter;

public final class BufferedWriterTools {

    public static void writeIgnoreExceptions (BufferedWriter bw, String text) {
        try {
            bw.write(text);
        } catch (Exception e) {

        }
    }

    public static void writeNewLine (BufferedWriter bw) {
        try {
            bw.newLine();
        } catch (Exception e) {

        }
    }
}
