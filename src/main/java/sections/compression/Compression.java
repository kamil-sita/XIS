package sections.compression;

public class Compression {

    private static final int ALG_NAME = 0x010571C2;
    private static final short VERSION = 0;
    private static final short LEGACY_VERSION = 0;

    public static void compress() {
        BitSequence b = generateHeader();
        DEBUG_printAsBits(b);
    }

    private static void DEBUG_printAsBits(BitSequence b) {
        var s = b.getSeq();
        int i = 0;
        for (Boolean c : s) {
            if (i%8 == 0 && i != 0) System.out.print(" ");
            i++;
            if (c) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }

        }
    }

    private static BitSequence generateHeader() {
        BitSequence b = new BitSequence();

        b.put(ALG_NAME, 32); //LOSTIC 2
        b.put(VERSION, 8); //current version
        b.put(LEGACY_VERSION, 8); //supported version, that is minimum version that old versions of algorithm may attempt reading
        int flag = 0;
        b.put(flag, 8);

        int width = 1024;
        int height = 1600;

        b.put(width, 32);
        b.put(height, 32);

        short blockSize = 16;
        b.put(blockSize, 16);
        return b;
    }


}
