package sections.compression;

import java.awt.image.BufferedImage;

public class CompressionOutput {
    private BitSequence bitSequence;
    private BufferedImage previewImage;
    private Statistic statistic;

    public CompressionOutput(BitSequence bitSequence, BufferedImage previewImage, Statistic statistic) {
        this.bitSequence = bitSequence;
        this.previewImage = previewImage;
        this.statistic = statistic;
    }

    public BitSequence getBitSequence() {
        return bitSequence;
    }

    public BufferedImage getPreviewImage() {
        return previewImage;
    }

    public Statistic getStatistic() {
        return statistic;
    }
}
