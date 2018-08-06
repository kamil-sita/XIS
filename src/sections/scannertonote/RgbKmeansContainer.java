package sections.scannerToNote;

import pl.kamilsitarski.simplekmeans.SimpleKMeansData;
import universal.tools.imagetools.bufferedimagetools.RGB;

import java.util.List;

public class KmeansRgb implements SimpleKMeansData {

    private RGB rgb;

    public KmeansRgb(RGB rgb) {
        this.rgb = rgb;
    }

    @Override
    public SimpleKMeansData getNewWithRandomData() {
        return new KmeansRgb(new RGB(
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255)
        ));
    }

    @Override
    public double distanceTo(SimpleKMeansData simpleKMeansData) {
        return rgb.getDistanceFrom(((KmeansRgb) simpleKMeansData).rgb);
    }

    @Override
    public SimpleKMeansData meanOfList(List<SimpleKMeansData> list) {
        return null;
    }
}
