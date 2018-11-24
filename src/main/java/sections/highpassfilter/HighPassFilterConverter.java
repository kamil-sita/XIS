package sections.highpassfilter;

import toolset.imagetools.*;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

public final class HighPassFilterConverter {

    public static BufferedImage convert(BufferedImage bufferedImage, int blurPasses, boolean scaleBrightness, double scaleBrightnessVal, boolean higherQuality) {

        if (higherQuality) blurPasses *= 2;

        if (higherQuality) bufferedImage = BufferedImageScale.getScaledImage(bufferedImage, 2);

        var blurredImage = BufferedImageVarious.copyImage(bufferedImage);

        Kernel kernel = BufferedImageBlur.genererateGausianKernel(blurPasses);

        blurredImage = BufferedImageBlur.simpleBlur(blurredImage, kernel);

        var output = BufferedImageLayers.divide(
                BufferedImageVarious.copyImage(bufferedImage), blurredImage);

        if (scaleBrightness) BufferedImageColorPalette.scaleAndCutoffBrightness(output, scaleBrightnessVal);
        if (higherQuality) output = BufferedImageScale.getScaledImage(output, 0.5);

        return output;
    }

}
