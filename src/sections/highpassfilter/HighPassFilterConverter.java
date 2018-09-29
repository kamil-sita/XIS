package sections.highpassfilter;

import toolset.imagetools.Blur;
import toolset.imagetools.BufferedImageLayers;
import toolset.imagetools.BufferedImageVarious;

import java.awt.image.BufferedImage;

public class HighPassFilterConverter {

    public static BufferedImage convert(BufferedImage bufferedImage, int blurPasses) {
        var blurredImage = BufferedImageVarious.copyImage(bufferedImage);

        for (int i = 0; i < blurPasses; i++) {
            blurredImage = Blur.simpleBlur(blurredImage);
        }

        var output = BufferedImageLayers.divide(BufferedImageVarious
                .copyImage(bufferedImage), blurredImage);
        return output;
    }

}
