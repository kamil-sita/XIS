package sections.compression;

public class Header {
    public int algName;
    public int version;
    public int legacyVersion;
    public int flag;
    public int width;
    public int height;
    public int blockSize;
    public boolean errors = false;

    public Header(int algName, int version, int legacyVersion, int flag, int width, int height, int blockSize) {
        this.algName = algName;
        this.version = version;
        this.legacyVersion = legacyVersion;
        this.flag = flag;
        this.width = width;
        this.height = height;
        this.blockSize = blockSize;
    }

    public Header(BitSequenceDecoder bitSequenceDecoder) {
        if (!bitSequenceDecoder.has(136)) {
            errors = true;
            return;
        }
        algName = bitSequenceDecoder.get(32);
        version = bitSequenceDecoder.get(8);
        legacyVersion = bitSequenceDecoder.get(8);
        flag = bitSequenceDecoder.get(8);
        width = bitSequenceDecoder.get(32);
        height = bitSequenceDecoder.get(32);
        blockSize = bitSequenceDecoder.get(16);
    }

    public void addToBitSequence(BitSequence b) {
        b.put(algName, 32);
        b.put(version, 8);
        b.put(legacyVersion, 8);
        b.put(flag, 8);
        b.put(width, 32);
        b.put(height, 32);
        b.put(blockSize, 16);
    }

}
