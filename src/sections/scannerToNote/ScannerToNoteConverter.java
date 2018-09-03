package sections.scannertonote;

import pl.ksitarski.simplekmeans.KMeans;
import toolset.imagetools.BufferedImagePalette;
import toolset.imagetools.RGB;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerToNoteConverter {

    public static BufferedImage convert(BufferedImage input, boolean filterBackground, boolean scaleBrightness) {

        final int COLORS = 256;
        final int DEPTH = 6;
        final int ITERATIONS = 64;

        final double MINIMUM_BRIGHTNESS_DIFFERENCE = 0.25;
        final double MINIMUM_SATURATION_DIFFERENCE = 0.25;

        input = copyImage(input);

        //get sample data by getting every 16 pixel (~6% of original)
        List<RgbContainer> rgbList = new ArrayList<>();

        for (int y = 0; y < input.getHeight(); y += 4) {
            for (int x = 0; x < input.getWidth(); x += 4) {
                rgbList.add(new RgbContainer(new RGB(input.getRGB(x, y))));
            }
        }

        final List<RgbContainer> SAMPLE_WITH_REDUCED_BITRATE = getCopyOf(rgbList);
        for (var rgb : SAMPLE_WITH_REDUCED_BITRATE) {
            rgb.reduceDepth(DEPTH);
        }

        RGB backgroundColor = null;

        if (filterBackground) {
            backgroundColor = getMostCommon(SAMPLE_WITH_REDUCED_BITRATE);
            filterBackgroungByBrightnessAndSaturation(rgbList, backgroundColor, MINIMUM_BRIGHTNESS_DIFFERENCE, MINIMUM_SATURATION_DIFFERENCE);
        }

        var kMeans = new KMeans<>(COLORS, rgbList);

        kMeans.iterateWithThreads(ITERATIONS, 32);

        List<RgbContainer> results = kMeans.getResults();

        for (RgbContainer rgbContainer : results) {
            System.out.println(rgbContainer.getRgb());
        }

        if (filterBackground) {
            results.add(new RgbContainer(backgroundColor));
        }

        BufferedImagePalette.replace(input, RgbContainer.toRgbList(results));
        if (scaleBrightness) {
            var rgbs = new ArrayList<RGB>();
            for (var rgbContainer : results) {
                rgbs.add(rgbContainer.getRgb());
            }
            BufferedImagePalette.scaleBrightness(input, rgbs);
        }

        return input;
    }

    private static List<RgbContainer> getCopyOf(List<RgbContainer> rgbContainers) {
        var copy = new ArrayList<RgbContainer>();
        for (var rgb : rgbContainers) {
            copy.add(rgb.copy());
        }
        return copy;
    }

    private static RGB getMostCommon(List<RgbContainer> rgbs) {
        //counting occurrences
        HashMap<RGB, Integer> rgbWithOccurrences = new HashMap<>();
        for (var rgb : rgbs) {
            if (rgbWithOccurrences.containsKey(rgb.getRgb())) {
                int newVal = rgbWithOccurrences.get(rgb.getRgb()) + 1;
                rgbWithOccurrences.put(rgb.getRgb(), newVal);
            } else {
                rgbWithOccurrences.put(rgb.getRgb(), 1);
            }
        }
        var entries = rgbWithOccurrences.entrySet();
        int mostCommonOccurrences = -1;
        var mostCommon = rgbs.get(0).getRgb();

        for (var entry : (Map.Entry<RGB, Integer>[]) entries.toArray()) {
            if (entry.getValue() > mostCommonOccurrences) {
                mostCommonOccurrences = entry.getValue();
                mostCommon = entry.getKey();
            }
        }

        return mostCommon;
    }

    private static BufferedImage copyImage(BufferedImage bufferedImage) {
        return bufferedImage.getSubimage(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    private static void filterBackgroungByBrightnessAndSaturation(List<RgbContainer> rgbContainerList, RGB backgroundColor, double minBrightnessDiff, double minSaturationDiff) {
        var hsbBackground = backgroundColor.toHSB();
        for (int i = 0; i < rgbContainerList.size(); i++) {
            var rgbC = rgbContainerList.get(i);
            var rgb = rgbC.getRgb();
            var hsb = rgb.toHSB();
            if (hsb.brightnessDiff(hsbBackground) > minBrightnessDiff) {
                rgbContainerList.remove(i);
                i--;
                continue;
            }
            if (hsb.saturationDiff(hsbBackground) > minSaturationDiff) {
                rgbContainerList.remove(i);
                i--;
            }

        }
    }


}
