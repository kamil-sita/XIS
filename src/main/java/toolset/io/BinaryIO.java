package toolset.io;

import sections.compression.BitSequence;

import java.io.FileOutputStream;
import java.io.IOException;


public class BinaryIO {
    public static boolean writeToSelectedByUser(BitSequence bitSequence) {
        var optionalOut = GuiFileIO.getSaveDirectory();
        if (optionalOut.isPresent()) {
            byte[] data = bitSequence.getSeqArray();
            try {
                FileOutputStream fos = new FileOutputStream(optionalOut.get());
                fos.write(data, 0, data.length);
                fos.close();
            } catch (IOException e) {
                return false;
            }

            return true;
        }
        return false;
    }
}
