package sections.highpassfilter;

import toolset.imagetools.Blur;
import toolset.imagetools.BufferedImageLayers;
import toolset.imagetools.BufferedImageVarious;
import toolset.imagetools.RGBImage;

import java.awt.image.BufferedImage;

public final class HighPassFilterConverter {

    public static RGBImage convert(BufferedImage bufferedImage, int blurPasses, boolean scaleBrightnessAndSaturation) {
        var blurredImage = BufferedImageVarious.copyImage(bufferedImage);

        for (int i = 0; i < blurPasses; i++) {
            blurredImage = Blur.simpleBlur(blurredImage);
        }

        var output = BufferedImageLayers.divide(
                BufferedImageVarious.copyImage(bufferedImage), blurredImage);

        if (scaleBrightnessAndSaturation) output.scaleBrightnessAndSaturation();

        return output;
    }

}
