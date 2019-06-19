package XIS.sections.compression;

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

    public Header(BitSequence bitSequence) {
        if (!bitSequence.has(136)) {
            errors = true;
            return;
        }

        algName = bitSequence.remove(32);
        version = bitSequence.remove(8);
        legacyVersion = bitSequence.remove(8);
        flag = bitSequence.remove(8);
        width = bitSequence.remove(32);
        height = bitSequence.remove(32);
        blockSize = bitSequence.remove(16);
    }

    public void addToBitSequence(BitSequence b) {
        b.insert(algName, 32);
        b.insert(version, 8);
        b.insert(legacyVersion, 8);
        b.insert(flag, 8);
        b.insert(width, 32);
        b.insert(height, 32);
        b.insert(blockSize, 16);
    }

}
