package toolset.imagetools;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

public final class HighPassFilterConverter {

    public static BufferedImage convert(BufferedImage bufferedImage, int blurPasses, boolean scaleBrightness, double scaleBrightnessVal) {

        var blurredImage = BufferedImageLayers.copyImage(bufferedImage);

        Kernel kernel = BufferedImageBlur.generateGaussianKernel(blurPasses);

        blurredImage = BufferedImageBlur.simpleBlur(blurredImage, kernel);

        var output = BufferedImageLayers.divide(
                BufferedImageLayers.copyImage(bufferedImage), blurredImage);

        if (scaleBrightness) BufferedImageColorPalette.scaleAndCutoffBrightness(output, scaleBrightnessVal);

        return output;
    }

}
