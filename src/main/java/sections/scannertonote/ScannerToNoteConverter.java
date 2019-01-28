package sections.scannertonote;

import pl.ksitarski.simplekmeans.KMeans;
import sections.Interruptible;
import sections.UserFeedback;
import toolset.imagetools.BufferedImageColorPalette;
import toolset.imagetools.BufferedImageVarious;
import toolset.imagetools.Rgb;

import java.awt.image.BufferedImage;
import java.util.*;

public final class ScannerToNoteConverter {

    public static Optional<BufferedImage> convert(ScannerToNoteParameters scannerToNoteParameters, Interruptible interruptable) {

        if (scannerToNoteParameters.getColors() <= 0) return Optional.empty();

        final int DEPTH = 5;
        final int ITERATIONS = 32;

        var inputCopy = BufferedImageVarious.copyImage(scannerToNoteParameters.getInput());

        List<RgbContainer> rgbList = sampleData(inputCopy);

        final List<RgbContainer> SAMPLE_WITH_REDUCED_BITRATE = reduceBitrate(DEPTH, rgbList);

        final Rgb BACKGROUND_COLOR = scannerToNoteParameters.isFilterBackground() ? getMostCommon(SAMPLE_WITH_REDUCED_BITRATE) : null;

        if (scannerToNoteParameters.isFilterBackground()) {
            filterOutBackgroundByBrightnessAndSaturation(rgbList, BACKGROUND_COLOR, scannerToNoteParameters.getBrightnessDiff(), scannerToNoteParameters.getSaturationDiff());
        }

        if (rgbList.size() == 0) {
            UserFeedback.popup("Not enough colors in the image. Consider turning off 'isolate background' option, if enabled.");
            return Optional.empty();
        }

        var kMeans = new KMeans<>(scannerToNoteParameters.getColors(), rgbList);

        kMeans.setOnUpdate(() -> {
            UserFeedback.reportProgress(kMeans.getProgress());
            if (interruptable.isInterrupted()) {
                kMeans.abort();
            }
        });

        kMeans.iterate(ITERATIONS);

        if (interruptable.isInterrupted()) {
            return Optional.empty();
        }

        List<RgbContainer> results = kMeans.getCalculatedMeanPoints();

        if (scannerToNoteParameters.isFilterBackground()) {
            results.add(new RgbContainer(BACKGROUND_COLOR));
        }

        BufferedImageColorPalette.replace(inputCopy, RgbContainer.toRgbList(results));

        scaleBrightnessIfNeeded(scannerToNoteParameters.isScaleBrightness(), inputCopy, results);

        if (interruptable.isInterrupted()) {
            return Optional.empty();
        }

        return Optional.of(inputCopy);
    }

    private static void scaleBrightnessIfNeeded(boolean scaleBrightness, BufferedImage inputCopy, List<RgbContainer> results) {
        if (scaleBrightness) {
            var rgbs = new ArrayList<Rgb>();
            for (var rgbContainer : results) {
                rgbs.add(rgbContainer.getRgb());
            }
            BufferedImageColorPalette.scaleBrightness(inputCopy, rgbs);
        }
    }

    private static List<RgbContainer> reduceBitrate(int DEPTH, List<RgbContainer> rgbList) {
        final List<RgbContainer> SAMPLE_WITH_REDUCED_BITRATE = getCopyOf(rgbList);
        for (var rgb : SAMPLE_WITH_REDUCED_BITRATE) {
            rgb.reduceDepth(DEPTH);
        }
        return SAMPLE_WITH_REDUCED_BITRATE;
    }

    private static List<RgbContainer> sampleData(BufferedImage inputCopy) {
        List<RgbContainer> rgbList = new ArrayList<>();
        for (int y = 0; y < inputCopy.getHeight(); y += 3) {
            for (int x = 0; x < inputCopy.getWidth(); x += 2) {
                var rgb = new Rgb(inputCopy.getRGB(x, y));
                rgbList.add(rgb.inContainer());
            }
        }
        return rgbList;
    }

    private static List<RgbContainer> getCopyOf(List<RgbContainer> rgbContainers) {
        var copy = new ArrayList<RgbContainer>();
        for (var rgb : rgbContainers) {
            copy.add(rgb.copy());
        }
        return copy;
    }

    private static Rgb getMostCommon(List<RgbContainer> rgbs) {
        //counting occurrences
        HashMap<Rgb, Integer> rgbWithOccurrences = new HashMap<>();
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
            var entry = (Map.Entry<Rgb, Integer>) entryo;
            if (entry.getValue() > mostCommonOccurrences) {
                mostCommonOccurrences = entry.getValue();
                mostCommon = entry.getKey();
            }
        }

        return mostCommon;
    }

    private static void filterOutBackgroundByBrightnessAndSaturation(List<RgbContainer> rgbContainerList, Rgb backgroundColor, double minBrightnessDiff, double minSaturationDiff) {
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


    static class ScannerToNoteParameters {
        private final BufferedImage input;
        private final boolean filterBackground;
        private final boolean scaleBrightness;
        private final int colors;
        private final double brightnessDiff;
        private final double saturationDiff;

        ScannerToNoteParameters(BufferedImage input, boolean filterBackground, boolean scaleBrightness, int colors, double brightnessDiff, double saturationDiff) {
            this.input = input;
            this.filterBackground = filterBackground;
            this.scaleBrightness = scaleBrightness;
            this.colors = colors;
            this.brightnessDiff = brightnessDiff;
            this.saturationDiff = saturationDiff;
        }

        public BufferedImage getInput() {
            return input;
        }

        public boolean isFilterBackground() {
            return filterBackground;
        }

        public boolean isScaleBrightness() {
            return scaleBrightness;
        }

        public int getColors() {
            return colors;
        }

        public double getBrightnessDiff() {
            return brightnessDiff;
        }

        public double getSaturationDiff() {
            return saturationDiff;
        }
    }
}
