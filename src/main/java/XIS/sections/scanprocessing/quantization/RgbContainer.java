package XIS.sections.scanprocessing.quantization;

import XIS.toolset.imagetools.Rgb;
import pl.ksitarski.simplekmeans.KMeansData;

import java.util.ArrayList;
import java.util.List;

public final class RgbContainer implements KMeansData {

    private Rgb rgbValue;

    public RgbContainer(Rgb rgb) {
        this.rgbValue = rgb;
    }

    @Override
    public double distanceTo(KMeansData simpleKMeansData) {
        return rgbValue.getDistanceFrom(((RgbContainer) simpleKMeansData).rgbValue);
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
