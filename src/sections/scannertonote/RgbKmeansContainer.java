package sections.scannertonote;

import pl.ksitarski.simplekmeans.KMeansData;
import toolset.imagetools.RGB;

import java.util.ArrayList;
import java.util.List;

public class RgbKmeansContainer implements KMeansData {

    private RGB rgbValue;

    public RgbKmeansContainer(RGB rgb) {
        this.rgbValue = rgb;
    }

    @Override
    public KMeansData getNewWithRandomData() {
        return new RgbKmeansContainer(new RGB(
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255)
        ));
    }

    @Override
    public double distanceTo(KMeansData simpleKMeansData) {
        return rgbValue.getDistanceFrom(((RgbKmeansContainer) simpleKMeansData).rgbValue);
    }

    @Override
    public KMeansData meanOfList(List<KMeansData> list) {
        if (list == null || list.size() == 0) return null;
        RGB rgb = new RGB(0, 0 ,0);
        for (KMeansData simpleKMeansData : list) {
            RGB rgbElement = ((RgbKmeansContainer) simpleKMeansData).rgbValue;
            rgb.r += rgbElement.r;
            rgb.g += rgbElement.g;
            rgb.b += rgbElement.b;
        }
        rgb.r /= list.size();
        rgb.g /= list.size();
        rgb.b /= list.size();
        return new RgbKmeansContainer(rgb);
    }

    public RGB getRgb() {
        return rgbValue;
    }

    public static List<RGB> toRgbList(List<RgbKmeansContainer> rgbContainerList) {
        List<RGB> rgbList = new ArrayList<>();
        for (RgbKmeansContainer rgbContainer : rgbContainerList) {
            rgbList.add(rgbContainer.getRgb());
        }
        return rgbList;
    }
}
