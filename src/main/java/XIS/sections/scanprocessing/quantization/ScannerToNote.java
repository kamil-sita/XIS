package XIS.sections.scanprocessing.quantization;

import XIS.sections.GlobalSettings;
import XIS.sections.Interruptible;
import XIS.toolset.imagetools.BufferedImageColorPalette;
import XIS.toolset.imagetools.BufferedImageLayers;
import XIS.toolset.imagetools.Rgb;
import pl.ksitarski.simplekmeans.KMeansBuilder;

import java.awt.image.BufferedImage;
import java.util.*;

public final class ScannerToNote {

    public static Optional<BufferedImage> convert(ScannerToNoteParameters scannerToNoteParameters, Interruptible interruptable) {

        if (scannerToNoteParameters.getColors() <= 0) return Optional.empty();


        final int DEPTH = 5;
        final int ITERATIONS = 32;

        var inputCopy = BufferedImageLayers.copyImage(scannerToNoteParameters.getInput());

        List<Rgb> rgbList = sampleData(inputCopy);

        final List<Rgb> SAMPLE_WITH_REDUCED_BITRATE = reduceBitrate(DEPTH, rgbList);

        final Rgb BACKGROUND_COLOR = scannerToNoteParameters.isFilterBackground() ? getMostCommon(SAMPLE_WITH_REDUCED_BITRATE) : null;

        if (scannerToNoteParameters.isFilterBackground()) {
            filterOutBackgroundByBrightnessAndSaturation(rgbList, BACKGROUND_COLOR, scannerToNoteParameters.getBrightnessDiff(), scannerToNoteParameters.getSaturationDiff());
        }

        if (rgbList.size() == 0) {
            interruptable.popup("Not enough colors in the image. Consider turning off 'isolate background' option, if enabled.");
            return Optional.empty();
        }


        var kMeans = new KMeansBuilder<>(
                rgbList,
                scannerToNoteParameters.getColors(),
                Rgb::meanOfList,
                Rgb::distance
        ).onUpdate(interruptable::reportProgress).setOptimizationSkipUpdatesBasedOnRange()
          .setThreadCount(GlobalSettings.getInstance().getNormalizedThreadCount())
          .build();


        Runnable listener = kMeans::earlyStop;
        interruptable.addListener(listener);

        kMeans.iterate(ITERATIONS);

        interruptable.removeListener(listener);

        if (interruptable.isInterrupted()) {
            return Optional.empty();
        }

        List<Rgb> results = kMeans.getCalculatedMeanPoints();

        if (scannerToNoteParameters.isFilterBackground()) {
            results.add(new Rgb(BACKGROUND_COLOR));
        }

        BufferedImageColorPalette.replace(inputCopy, results);

        scaleBrightnessIfNeeded(scannerToNoteParameters.isScaleBrightness(), inputCopy, results);

        if (scannerToNoteParameters.isInverted()) {
            BufferedImageColorPalette.invert(inputCopy);
        }


        if (interruptable.isInterrupted()) {
            return Optional.empty();
        }

        return Optional.of(inputCopy);
    }

    private static void scaleBrightnessIfNeeded(boolean scaleBrightness, BufferedImage inputCopy, List<Rgb> results) {
        if (scaleBrightness) {
            BufferedImageColorPalette.scaleBrightness(inputCopy, results);
        }
    }

    private static List<Rgb> reduceBitrate(int DEPTH, List<Rgb> rgbList) {
        final List<Rgb> SAMPLE_WITH_REDUCED_BITRATE = getDeepCopyOf(rgbList);
        for (var rgb : SAMPLE_WITH_REDUCED_BITRATE) {
            rgb.reduceDepth(DEPTH);
        }
        return SAMPLE_WITH_REDUCED_BITRATE;
    }

    private static List<Rgb> sampleData(BufferedImage inputCopy) {
        List<Rgb> rgbList = new ArrayList<>();
        for (int y = 0; y < inputCopy.getHeight(); y += 3) {
            for (int x = 0; x < inputCopy.getWidth(); x += 2) {
                var rgb = new Rgb(inputCopy.getRGB(x, y));
                rgbList.add(rgb);
            }
        }
        return rgbList;
    }

    private static List<Rgb> getDeepCopyOf(List<Rgb> rgbContainers) {
        var copy = new ArrayList<Rgb>();
        for (var rgb : rgbContainers) {
            copy.add(new Rgb(rgb));
        }
        return copy;
    }

    private static Rgb getMostCommon(List<Rgb> rgbs) {
        //counting occurrences
        HashMap<Rgb, Integer> rgbWithOccurrences = new HashMap<>();
        for (var rgb : rgbs) {
            if (rgbWithOccurrences.containsKey(rgb)) {
                int newVal = rgbWithOccurrences.get(rgb) + 1;
                rgbWithOccurrences.put(rgb, newVal);
            } else {
                rgbWithOccurrences.put(rgb, 1);
            }
        }
        var entries = rgbWithOccurrences.entrySet();
        int mostCommonOccurrences = -1;
        var mostCommon = rgbs.get(0);
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

    private static void filterOutBackgroundByBrightnessAndSaturation(List<Rgb> rgbContainerList, Rgb backgroundColor, double minBrightnessDiff, double minSaturationDiff) {
        var hsbBackground = backgroundColor.toHSB();
        for (int i = 0; i < rgbContainerList.size(); i++) {
            var rgb = rgbContainerList.get(i);
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
