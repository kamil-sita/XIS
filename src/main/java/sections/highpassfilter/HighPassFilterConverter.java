package sections.highpassfilter;

import toolset.imagetools.BufferedImageBlur;
import toolset.imagetools.BufferedImageColorPalette;
import toolset.imagetools.BufferedImageLayers;
import toolset.imagetools.BufferedImageVarious;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

public final class HighPassFilterConverter {

    public static BufferedImage convert(BufferedImage bufferedImage, int blurPasses, boolean scaleBrightness, double scaleBrightnessVal) {

        var blurredImage = BufferedImageVarious.copyImage(bufferedImage);

        Kernel kernel = BufferedImageBlur.generateGaussianKernel(blurPasses);

        blurredImage = BufferedImageBlur.simpleBlur(blurredImage, kernel);

        var output = BufferedImageLayers.divide(
                BufferedImageVarious.copyImage(bufferedImage), blurredImage);

        if (scaleBrightness) BufferedImageColorPalette.scaleAndCutoffBrightness(output, scaleBrightnessVal);

        return output;
    }

}
