package XIS.toolset.scanfilters;

import XIS.sections.Interruptible;
import XIS.toolset.imagetools.BufferedImageBlur;
import XIS.toolset.imagetools.BufferedImageColorPalette;
import XIS.toolset.imagetools.BufferedImageLayers;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

public class HighPassFilter implements Filter {

    private HighPassFilterArguments arguments;

    public HighPassFilter(HighPassFilterArguments arguments) {
        this.arguments = arguments;
    }

    @Override
    public BufferedImage filter(BufferedImage input, Interruptible interruptible) {

        return convert(
                input,
                arguments.getBlurPasses(),
                arguments.getScaleBrightnessVal(),
                arguments.isBlackAndWhite()
        );
    }

    private static BufferedImage convert(BufferedImage bufferedImage, int blurPasses, double scaleBrightnessVal, boolean blackAndWhite) {
        long time = System.nanoTime();
        bufferedImage = changeToIntArgb(bufferedImage);

        if (blackAndWhite) {
            BufferedImageLayers.convertToGrayscale(bufferedImage);
        }

        var blurredImage = BufferedImageLayers.copyImage(bufferedImage);

        Kernel kernel = BufferedImageBlur.generateGaussianKernel(blurPasses);

        blurredImage = BufferedImageBlur.simpleBlur(blurredImage, kernel);

        var output = BufferedImageLayers.divide(
                BufferedImageLayers.copyImage(bufferedImage), blurredImage);

        BufferedImageColorPalette.scaleAndCutoffBrightness(output, scaleBrightnessVal);

        System.out.println((System.nanoTime() - time) / 1_000_000_000.0 + "s");

        return output;
    }

    private static BufferedImage changeToIntArgb(BufferedImage in) {
        BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
        out.getGraphics().drawImage(in, 0, 0, null);
        return out;
    }
}
