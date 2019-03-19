package XIS.sections.scannertonote;

import XIS.toolset.imagetools.Rgb;
import pl.ksitarski.simplekmeans.KMeansData;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public final class RgbContainer implements KMeansData {

    private Rgb rgbValue;

    public RgbContainer(Rgb rgb) {
        this.rgbValue = rgb;
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
        Rgb rgb = new Rgb(0, 0 ,0);
        for (KMeansData simpleKMeansData : list) {
            Rgb rgbElement = ((RgbContainer) simpleKMeansData).rgbValue;
            rgb.r += rgbElement.r;
            rgb.g += rgbElement.g;
            rgb.b += rgbElement.b;
        }
        rgb.r /= list.size();
        rgb.g /= list.size();
        rgb.b /= list.size();
        return new RgbContainer(rgb);
    }

    public Rgb getRgb() {
        return rgbValue;
    }

    public static List<Rgb> toRgbList(List<RgbContainer> rgbContainerList) {
        List<Rgb> rgbList = new ArrayList<>();
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
