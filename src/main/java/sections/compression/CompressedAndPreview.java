package sections.compression;

import java.awt.image.BufferedImage;

public class CompressedAndPreview {
    private BitSequence bitSequence;
    private BufferedImage bufferedImage;

    public CompressedAndPreview(BitSequence bitSequence, BufferedImage bufferedImage) {
        this.bitSequence = bitSequence;
        this.bufferedImage = bufferedImage;
    }

    public BitSequence getBitSequence() {
        return bitSequence;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}
