package sections.scannertonote;

import pl.ksitarski.simplekmeans.KMeans;
import toolset.imagetools.BufferedImagePalette;
import toolset.imagetools.RGB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScannerToNoteConverter {

    public BufferedImage convert() {

        final int COLORS = 256;
        final int DEPTH = 6;
        final int ITERATIONS = 64;

        final double MINIMUM_BRIGHTNESS_DIFFERENCE = 0.25;
        final double MINIMUM_SATURATION_DIFFERENCE = 0.25;

        final boolean FILTER_BACKGROUND = false;
        final boolean SCALE_BRIGHTNESS = false;

        final String INPUT = "input.png";

        BufferedImage input = null;
        try {
            input = ImageIO.read(new File(INPUT));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //get sample data by getting every 4th pixel (~25% of original)
        List<RgbKmeansContainer> rgbList = new ArrayList<>();

        for (int y = 0; y < input.getHeight(); y += 4) {
            for (int x = 0; x < input.getWidth(); x += 4) {
                rgbList.add(new RgbKmeansContainer(new RGB(input.getRGB(x, y))));
            }
        }

        //final List<RgbKmeansContainer> SAMPLE_WITH_REDUCED_BITRATE = sample.getCopy();
        //for (RGB rgb : SAMPLE_WITH_REDUCED_BITRATE.getList()) {
        //    rgb.reduceDepth(DEPTH);
        //}
//
        //final RGB BACKGROUND_COLOR;
        //if (FILTER_BACKGROUND) {
        //    BACKGROUND_COLOR = SAMPLE_WITH_REDUCED_BITRATE.getMostCommon();
        //    sample.filterOutBrightnessSaturation(BACKGROUND_COLOR, MINIMUM_BRIGHTNESS_DIFFERENCE, MINIMUM_SATURATION_DIFFERENCE);
        //}



        KMeans<RgbKmeansContainer> kMeans = new KMeans(COLORS, rgbList);

        kMeans.iterateWithThreads(ITERATIONS, 32);

        List<RgbKmeansContainer> results = kMeans.getResults();

        for (RgbKmeansContainer rgbContainer : results) {
            System.out.println(rgbContainer.getRgb());
        }

        //if (FILTER_BACKGROUND) {
        //    kMeansPoints.addRgb(BACKGROUND_COLOR);
        //}

        BufferedImagePalette.replace(input, RgbKmeansContainer.toRgbList(results));
        //if (SCALE_BRIGHTNESS) {
        //    BufferedImagePalette.scaleBrightness(input, kMeansPoints);
        //}


        try {
            ImageIO.write(input, "png", new File("output.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished");
        System.exit(0);
        return null;
    }
}
