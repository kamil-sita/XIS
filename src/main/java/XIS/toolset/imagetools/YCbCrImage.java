package XIS.toolset.imagetools;

import java.awt.image.BufferedImage;

public class YCbCrImage {

    private YCbCrLayer yl;
    private YCbCrLayer cbl;
    private YCbCrLayer crl;

    private int width;
    private int height;

    public YCbCrImage(BufferedImage bufferedImage) {
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();

        yl = new YCbCrLayer(width, height);
        cbl = new YCbCrLayer(width, height);
        crl = new YCbCrLayer(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var rgb = new Rgb(bufferedImage.getRGB(x, y));
                var ycbcr = new YCbCr(rgb);

                yl.set(x, y, ycbcr.y);
                cbl.set(x, y, ycbcr.cb);
                crl.set(x, y, ycbcr.cr);
            }
        }
    }

    public YCbCrImage(YCbCrLayer yl, YCbCrLayer cbl, YCbCrLayer crl) {
        this.yl = yl;
        this.cbl = cbl;
        this.crl = crl;
        width = yl.width();
        height = yl.height();
    }

    public YCbCrImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.yl = new YCbCrLayer(width, height);
        this.cbl = new YCbCrLayer(width, height);
        this.crl = new YCbCrLayer(width, height);
    }

    public BufferedImage getBufferedImage() {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var ycbcr = new YCbCr(yl.get(x, y), cbl.get(x, y), crl.get(x, y));
                bufferedImage.setRGB(x, y, ycbcr.toRgb().normalize().toInt());
            }
        }

        return bufferedImage;
    }

    public YCbCrLayer getYl() {
        return yl;
    }

    public YCbCrLayer getCbl() {
        return cbl;
    }

    public YCbCrLayer getCrl() {
        return crl;
    }

}
