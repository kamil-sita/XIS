package XIS.toolset.imagetools;

public class YCbCr {
    public int y;
    public int cb;
    public int cr;

    public YCbCr(int y, int cb, int cr) {
        this.y = y;
        this.cb = cb;
        this.cr = cr;
    }

    public YCbCr(Rgb rgb) {
        int r = rgb.r;
        int g = rgb.g;
        int b = rgb.b;

        y  = ((int) (0.299* r + 0.587 * g + 0.114 * b));
        cb = (128 + (int) (-0.168736 * r - 0.331264 * g + 0.5* b));
        cr = (128 + (int) (0.5* r - 0.418688 * g - 0.081312* b));
        normalize();
    }

    public YCbCr normalize() {

        y = Math.max(0, y);
        y = Math.min(255, y);

        cb = Math.max(0, cb);
        cb = Math.min(255, cb);

        cr = Math.max(0, cr);
        cr = Math.min(255, cr);

        return this;
    }

    public Rgb toRgb() {
        normalize();
        int r = (int) (y + 1.402 * (cr - 128));
        int g = (int) (y - 0.344136 * (cb - 128) - 0.714136 * (cr - 128));
        int b = (int) (y + 1.772 * (cb - 128));
        return new Rgb(r, g, b);
    }


}
