package XIS.toolset.imagetools;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

public final class HighPassFilterConverter {

    public static BufferedImage convert(BufferedImage bufferedImage, int blurPasses, boolean scaleBrightness, double scaleBrightnessVal) {
        long time = System.nanoTime();
        bufferedImage = changeToIntArgb(bufferedImage);
        var blurredImage = BufferedImageLayers.copyImage(bufferedImage);

        Kernel kernel = BufferedImageBlur.generateGaussianKernel(blurPasses);

        blurredImage = BufferedImageBlur.simpleBlur(blurredImage, kernel);

        var output = BufferedImageLayers.divide(
                BufferedImageLayers.copyImage(bufferedImage), blurredImage);

        if (scaleBrightness) BufferedImageColorPalette.scaleAndCutoffBrightness(output, scaleBrightnessVal);

        System.out.println((System.nanoTime() - time) / 1_000_000_000.0 + "s");

        return output;
    }

    private static BufferedImage changeToIntArgb(BufferedImage in) {
        BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
        out.getGraphics().drawImage(in, 0, 0, null);
        return out;
    }

}
