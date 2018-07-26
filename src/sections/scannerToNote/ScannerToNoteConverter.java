package sections.scannerToNote;

import universal.tools.imagetools.bufferedimagetools.BufferedImagePalette;
import universal.tools.imagetools.bufferedimagetools.RGB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
public class ScannerToNoteConverter {

    public BufferedImage convert() {

        final int COLORS = 256;
        final int DEPTH = 6;
        final int ITERATIONS = 32;

        final double MINIMUM_BRIGHTNESS_DIFFERENCE = 0.25;
        final double MINIMUM_SATURATION_DIFFERENCE = 0.25;

        final boolean FILTER_BACKGROUND = false;
        final boolean SCALE_BRIGHTNESS = false;

        BufferedImage input = null;
        try {
            input = ImageIO.read(new File("input.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //get sample data by getting every 4th pixel (~25% of original)
        RGBList sample = new RGBList();

        for (int y = 0; y < input.getHeight(); y += 2) {
            for (int x = 0; x < input.getWidth(); x += 2) {
                sample.addRgb(new RGB(input.getRGB(x, y)));
            }
        }

        final RGBList SAMPLE_WITH_REDUCED_BITRATE = sample.getCopy();
        for (RGB rgb : SAMPLE_WITH_REDUCED_BITRATE.getList()) {
            rgb.reduceDepth(DEPTH);
        }

        final RGB BACKGROUND_COLOR;
        if (FILTER_BACKGROUND) {
            BACKGROUND_COLOR = SAMPLE_WITH_REDUCED_BITRATE.getMostCommon();
            sample.filterOutBrightnessSaturation(BACKGROUND_COLOR, MINIMUM_BRIGHTNESS_DIFFERENCE, MINIMUM_SATURATION_DIFFERENCE);
        }


        KMeans kMeans = new KMeans(COLORS, sample);

        for (int i = 0; i < ITERATIONS; i++) {
            kMeans.iteration();
        }

        RGBList kMeansPoints = kMeans.getkMeansPoints();

        for (RGB rgb : kMeansPoints.rgbList) {
            System.out.println(rgb);
        }

        if (FILTER_BACKGROUND) {
            kMeansPoints.addRgb(BACKGROUND_COLOR);
        }

        BufferedImagePalette.replace(input, kMeansPoints);
        if (SCALE_BRIGHTNESS) {
            BufferedImagePalette.scaleBrightness(input, kMeansPoints);
        }


        try {
            ImageIO.write(input, "png", new File("output.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished");
        return null;
    }
}
