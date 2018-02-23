package universal.tools.BufferedImageTools;

public class RGB {

    public int a = 255;

    public int r;
    public int g;
    public int b;

    public RGB (int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public RGB (int r, int g, int b, int a) {
        this(r, g, b);
        this.a = a;
    }

    public RGB (int rgba) {
        int a = (rgba >> 24) & 0xFF;
        int r = (rgba >> 16) & 0xFF;
        int g = (rgba >> 8) & 0xFF;
        int b = rgba & 0xFF;
    }

    public int getRGBA() {
        int rgba = a;
        rgba = (rgba << 8) + r;
        rgba = (rgba << 8) + g;
        rgba = (rgba << 8) + b;
        return rgba;
    }
}
