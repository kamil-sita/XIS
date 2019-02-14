package toolset.io;

import sections.compression.BitSequence;

import java.io.*;
import java.util.Optional;


public class BinaryIO {

    private final static String FORMAT = "*.ls2";

    public static boolean writeBitSequenceToUserSelected(BitSequence bitSequence) {
        var optionalOut = GuiFileIO.getSaveDirectory(FORMAT);
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

    public static Optional<BitSequence> getBitSequenceFromUserSelected() {
        var file = GuiFileIO.getLoadDirectory(FORMAT);
        if (file.isPresent()) {

            BufferedInputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(file.get()));
            } catch (FileNotFoundException e) {
                return Optional.empty();
            }
            var data = readBinaryStream(is);
            return Optional.of(new BitSequence(data));
        }
        return Optional.empty();
    }

    private static byte[] readBinaryStream(InputStream input){
        byte[] tmp = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            try (input) {
                byteArrayOutputStream = new ByteArrayOutputStream(tmp.length);
                int bytesRead = 0;
                while (bytesRead != -1) {
                    bytesRead = input.read(tmp);
                    if (bytesRead > 0) {
                        byteArrayOutputStream.write(tmp, 0, bytesRead);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

}
