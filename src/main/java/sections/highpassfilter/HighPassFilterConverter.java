package sections.highpassfilter;

import toolset.imagetools.*;

import java.awt.image.BufferedImage;

public final class HighPassFilterConverter {

    public static BufferedImage convert(BufferedImage bufferedImage, int blurPasses, boolean scaleBrightness, double scaleBrightnessVal, boolean higherQuality) {

        if (higherQuality) blurPasses *= 2;

        if (higherQuality) bufferedImage = BufferedImageScale.getScaledImage(bufferedImage, 2);

        var blurredImage = BufferedImageVarious.copyImage(bufferedImage);
        int i = 0;

        for (i = 0; i < blurPasses; i++) {
            blurredImage = BufferedImageBlur.simpleBlur(blurredImage);
        }

        var output = BufferedImageLayers.divide(
                BufferedImageVarious.copyImage(bufferedImage), blurredImage);

        if (scaleBrightness) BufferedImageColorPalette.scaleAndCutoffBrightness(output, scaleBrightnessVal);
        if (higherQuality) output = BufferedImageScale.getScaledImage(output, 0.5);

        return output;
    }

}
