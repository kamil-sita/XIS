package sections.scannerToNote;

import universal.tools.imagetools.bufferedimagetools.HSB;
import universal.tools.imagetools.bufferedimagetools.RGB;

import java.util.*;

public class RGBList {

    List<RGB> rgbList = new ArrayList<>();

    public RGBList() {

    }

    public void addRgb(RGB rgb) {
        rgbList.add(rgb);
    }

    public void sort(Comparator comparator) {
        rgbList.sort(comparator);
    }

    public RGBList getCopy() {
        RGBList rgbList = new RGBList();
        for (RGB rgb : this.rgbList) {
            rgbList.addRgb(rgb.getCopy());
        }
        return rgbList;
    }

    public RGB getMostCommon() {
        Map<Integer, Integer> map = new HashMap<>();
        for (RGB rgb : rgbList) {
            Integer appearanceCount = map.get(rgb.toInt());
            if (appearanceCount == null) {
                map.put(rgb.toInt(), 1);
            } else {
                map.put(rgb.toInt(), appearanceCount + 1);
            }
        }

        Map.Entry<Integer, Integer> max = null;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (max == null || entry.getValue() > max.getValue()) {
                max = entry;
            }
        }


        for (RGB rgb : rgbList) {
            if (rgb.toInt() == max.getKey()) {
                System.out.println(rgb);
                return rgb;
            }
        }

        return null;
    }

    public RGB getAverage() {

        if (rgbList.size() == 0) return null;

        RGB averagedRgb = new RGB();
        averagedRgb.r = 0;
        averagedRgb.g = 0;
        averagedRgb.b = 0;

        for (RGB rgb : rgbList) {
            averagedRgb.r += rgb.r;
            averagedRgb.g += rgb.g;
            averagedRgb.b += rgb.b;
        }

        averagedRgb.r /= rgbList.size();
        averagedRgb.g /= rgbList.size();
        averagedRgb.b /= rgbList.size();


        return averagedRgb;
    }

    public RGB getClosestTo(RGB rgb) {
        RGB closest = null;
        for (RGB rgbInList : rgbList) {
            if (closest == null || rgbInList.getDistanceFrom(rgb) < closest.getDistanceFrom(rgb)) {
                closest = rgbInList;
            }
        }
        return closest;
    }


    public int getIdOf(RGB rgb) {
        for (int i = 0; i < rgbList.size(); i++) {
            if (rgbList.get(i).equals(rgb)) {
                return i;
            }
        }
        return -1;
    }

    public void filterOutBrightnessSaturation(RGB rgb, double minimumBrightnessDifference, double minimumSaturationDifference) {
        HSB hsb = rgb.toHSB();
        for (int i = 0; i < rgbList.size(); i++) {
            HSB hsbFromListElement = rgbList.get(i).toHSB();
            if (Math.abs(hsb.B - hsbFromListElement.B) <= minimumBrightnessDifference && Math.abs(hsb.S - hsbFromListElement.S) <= minimumSaturationDifference) {
                rgbList.remove(i);
                i--;
            }
        }
    }

    public RGB getAt(int index) {
        return rgbList.get(index);
    }

    public List<RGB> getList() {
        return rgbList;
    }

    //formula for scaling brigtness: (original - scaleDifferentiation) * scaleMultiplication
    public double getScaleDifferentiation() {
        double minBrightness = -1;

        for (RGB rgb : rgbList) {
            HSB hsb = rgb.toHSB();
            if (minBrightness == -1 || hsb.B < minBrightness) {
                minBrightness = hsb.B;
            }
        }

        return minBrightness;
    }

    public double getScaleMultiplication() {
        double minBrightness = getScaleDifferentiation();
        double maxBrightness = -1;

        for (RGB rgb : rgbList) {
            HSB hsb = rgb.toHSB();
            if (maxBrightness == -1 || hsb.B > maxBrightness) {
                maxBrightness = hsb.B;
            }
        }
        return  1.0 / (maxBrightness - minBrightness);
    }

}
