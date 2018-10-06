package sections.highpassfilter;

import sections.UserFeedback;
import toolset.imagetools.Blur;
import toolset.imagetools.BufferedImageLayers;
import toolset.imagetools.BufferedImageVarious;
import toolset.imagetools.RGBImage;

import java.awt.image.BufferedImage;

public final class HighPassFilterConverter {

    public static RGBImage convert(BufferedImage bufferedImage, int blurPasses, boolean scaleBrightness) {
        var blurredImage = BufferedImageVarious.copyImage(bufferedImage);
        int i = 0;
        UserFeedback.reportProgress(i/(blurPasses + 2.0));

        for (i = 0; i < blurPasses; i++) {
            blurredImage = Blur.simpleBlur(blurredImage);
            UserFeedback.reportProgress(i/(blurPasses + 2.0));
        }

        var output = BufferedImageLayers.divide(
                BufferedImageVarious.copyImage(bufferedImage), blurredImage);
        UserFeedback.reportProgress((i+1)/(blurPasses + 2.0));

        if (scaleBrightness) output.scaleBrightness();
        UserFeedback.reportProgress(1);

        return output;
    }

}
