package universal.tools;

import java.io.BufferedWriter;

public class BufferedWriterTools {

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
