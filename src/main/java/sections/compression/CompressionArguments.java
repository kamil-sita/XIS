package sections.compression;

import java.awt.image.BufferedImage;

public class CompressionArguments {
    private double yWeight;
    private double cWeight;
    private double blockSize;
    private BufferedImage input;
    private boolean allowReordering;

    public double getyWeight() {
        return yWeight;
    }

    public void setyWeight(double yWeight) {
        this.yWeight = yWeight;
    }

    public double getcWeight() {
        return cWeight;
    }

    public void setcWeight(double cWeight) {
        this.cWeight = cWeight;
    }

    public double getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(double blockSize) {
        this.blockSize = blockSize;
    }

    public BufferedImage getInput() {
        return input;
    }

    public void setInput(BufferedImage input) {
        this.input = input;
    }

    public boolean allowReordering() {
        return allowReordering;
    }

    public void setAllowReordering(boolean allowReordering) {
        this.allowReordering = allowReordering;
    }
}
