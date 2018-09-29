package sections.scannertonote;

import pl.ksitarski.simplekmeans.KMeans;
import sections.UserFeedback;
import toolset.imagetools.BufferedImagePalette;
import toolset.imagetools.RGB;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerToNoteConverter {

    public static BufferedImage convert(BufferedImage input, boolean filterBackground, boolean scaleBrightness, int colors, double brightnessDiff, double saturationDiff) {

        if (colors <= 0) return null;

        final int DEPTH = 5;
        final int ITERATIONS = 128;

        var inputCopy = copyImage(input);

        //get sample data by getting every 16th pixel (~6% of original)
        List<RgbContainer> rgbList = new ArrayList<>();

        for (int y = 0; y < inputCopy.getHeight(); y += 4) {
            for (int x = 0; x < inputCopy.getWidth(); x += 4) {
                var rgb = new RGB(inputCopy.getRGB(x, y));
                rgbList.add(rgb.inContainer());
            }
        }

        //reducing bitrate of selected sample data
        final List<RgbContainer> SAMPLE_WITH_REDUCED_BITRATE = getCopyOf(rgbList);
        for (var rgb : SAMPLE_WITH_REDUCED_BITRATE) {
            rgb.reduceDepth(DEPTH);
        }

        RGB backgroundColor = null;


        if (filterBackground) {
            backgroundColor = getMostCommon(SAMPLE_WITH_REDUCED_BITRATE);
            filterOutBackgroundByBrightnessAndSaturation(rgbList, backgroundColor, brightnessDiff, saturationDiff);
        }

        var kMeans = new KMeans<>(colors, rgbList);
        kMeans.setOnUpdate(() -> {
            UserFeedback.reportProgress(kMeans.getProgress());
        }, false);

        kMeans.iterate(ITERATIONS);

        List<RgbContainer> results = kMeans.getResults();

        if (filterBackground) {
            results.add(new RgbContainer(backgroundColor));
        }

        BufferedImagePalette.replace(inputCopy, RgbContainer.toRgbList(results));

        if (scaleBrightness) {
            var rgbs = new ArrayList<RGB>();
            for (var rgbContainer : results) {
                rgbs.add(rgbContainer.getRgb());
            }
            BufferedImagePalette.scaleBrightness(inputCopy, rgbs);
        }

        return inputCopy;
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
        var entriesArray = entries.toArray();

        for (var entryo : entriesArray) {
            var entry = (Map.Entry<RGB, Integer>) entryo;
            if (entry.getValue() > mostCommonOccurrences) {
                mostCommonOccurrences = entry.getValue();
                mostCommon = entry.getKey();
            }
        }

        return mostCommon;
    }

    private static BufferedImage copyImage(BufferedImage input) {
        BufferedImage imageCopy = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
        Graphics2D g = imageCopy.createGraphics();
        g.drawImage(input, 0, 0, null);
        g.dispose();
        return imageCopy;
    }

    private static void filterOutBackgroundByBrightnessAndSaturation(List<RgbContainer> rgbContainerList, RGB backgroundColor, double minBrightnessDiff, double minSaturationDiff) {
        var hsbBackground = backgroundColor.toHSB();
        for (int i = 0; i < rgbContainerList.size(); i++) {
            var rgbC = rgbContainerList.get(i);
            var rgb = rgbC.getRgb();
            var hsb = rgb.toHSB();
            if (hsb.brightnessDiff(hsbBackground) < minBrightnessDiff) {
                rgbContainerList.remove(i);
                i--;
                continue;
            }
            if (hsb.saturationDiff(hsbBackground) < minSaturationDiff) {
                rgbContainerList.remove(i);
                i--;
            }

        }
    }


}
