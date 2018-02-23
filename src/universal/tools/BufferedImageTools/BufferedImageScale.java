package universal.tools.BufferedImageTools;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BufferedImageScale {


    /**
     * Scales down image
     * @param source input BufferedImage
     * @param SIZE size of scaled down image
     * @return
     */

    public static BufferedImage getScaledDownImage(BufferedImage source, final int SIZE) {
        if (source == null) return null;
        BufferedImage newImage = new BufferedImage(SIZE, SIZE, source.getType());

        int sampleWidth = source.getWidth() / SIZE;
        int sampleHeight = source.getHeight() / SIZE;

        for (int x = 0; x < source.getWidth(); x += sampleWidth) {
            for (int y = 0; y < source.getHeight(); y += sampleHeight) {

                int xs = x / sampleWidth;
                int ys = y / sampleHeight;

                newImage.setRGB(xs, ys, getAverageRGB(x, y, source, SIZE).getRGBA());

            }
        }

        return newImage;
    }

    private static RGB getAverageRGB (int x, int y, BufferedImage source, final int SIZE) {

        int sampleWidth = source.getWidth() / SIZE;
        int sampleHeight = source.getHeight() / SIZE;

        ArrayList<RGB> sample = new ArrayList<>();

        for (int i = 0; i < sampleWidth; i++) {
            for (int j = 0; j < sampleHeight; j++) {

                if (x + i < source.getWidth() && y + j < source.getHeight()) {
                    sample.add(new RGB(source.getRGB(x + y, y + j)));
                }

            }
        }

        int combinedR = 0;
        int combinedG = 0;
        int combinedB = 0;


        for (RGB rgb : sample) {
            combinedR += rgb.r;
            combinedG += rgb.g;
            combinedB += rgb.b;
        }

        combinedR /= sample.size();
        combinedG /= sample.size();
        combinedB /= sample.size();

        return new RGB(combinedR, combinedG, combinedB);
    }

}