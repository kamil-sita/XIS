package sections.scannertonote;

import pl.ksitarski.simplekmeans.KMeansData;
import toolset.imagetools.RGB;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class RgbContainer implements KMeansData {

    private RGB rgbValue;

    public RgbContainer(RGB rgb) {
        this.rgbValue = rgb;
    }

    @Override
    public KMeansData getNewWithRandomData() {
        return new RgbContainer(new RGB(
                (int) (random() * 255),
                (int) (random() * 255),
                (int) (random() * 255)
        ));
    }

    @Override
    public double distanceTo(KMeansData simpleKMeansData) {
        if (false) {
            var hsb = ((RgbContainer) simpleKMeansData).rgbValue.toHSB();
            var hsb1 = rgbValue.toHSB();

            return sqrt(pow(hsb.B - hsb1.B, 2) + pow(hsb.S - hsb1.S, 2) + pow(hsb.H - hsb1.H, 2));
        } else {
            return rgbValue.getDistanceFrom(((RgbContainer) simpleKMeansData).rgbValue);
        }

    }



    @Override
    public KMeansData meanOfList(List<KMeansData> list) {
        if (list == null || list.size() == 0) return null;
        RGB rgb = new RGB(0, 0 ,0);
        for (KMeansData simpleKMeansData : list) {
            RGB rgbElement = ((RgbContainer) simpleKMeansData).rgbValue;
            rgb.r += rgbElement.r;
            rgb.g += rgbElement.g;
            rgb.b += rgbElement.b;
        }
        rgb.r /= list.size();
        rgb.g /= list.size();
        rgb.b /= list.size();
        return new RgbContainer(rgb);
    }

    public RGB getRgb() {
        return rgbValue;
    }

    public static List<RGB> toRgbList(List<RgbContainer> rgbContainerList) {
        List<RGB> rgbList = new ArrayList<>();
        for (RgbContainer rgbContainer : rgbContainerList) {
            rgbList.add(rgbContainer.getRgb());
        }
        return rgbList;
    }

    public RgbContainer copy() {
        return new RgbContainer(this.rgbValue);
    }

    public void reduceDepth(int depth) {
        rgbValue.reduceDepth(depth);
    }
}
