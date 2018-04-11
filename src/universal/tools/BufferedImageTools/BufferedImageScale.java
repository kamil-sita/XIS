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

        for (int xw = 0; xw < SIZE; xw++) {
            for (int yw = 0; yw < SIZE; yw++) {

                int x = xw * sampleWidth;
                int y = yw * sampleHeight;

                int color = getAverageRgbOfTile(x, y, source, SIZE).getInt();

                newImage.setRGB(xw, yw, color);

            }
        }

        return newImage;
    }

    /**
     * Returns average RGB of given tile of size SIZE, starting at (x, y)
     * @param x coordinate
     * @param y coordinate
     * @param source input BufferedImage on which average value of given pixel should be found
     * @param SIZE size of tile
     * @return average RGB
     */

    private static RGB getAverageRgbOfTile(int x, int y, BufferedImage source, final int SIZE) {

        int sampleWidth = source.getWidth() / SIZE;
        int sampleHeight = source.getHeight() / SIZE;

        ArrayList<RGB> sample = new ArrayList<>();

        //Gettting samples from BufferedImage
        for (int i = 0; i < sampleWidth; i++) {
            for (int j = 0; j < sampleHeight; j++) {

                if (x + i < source.getWidth() && y + j < source.getHeight()) {
                    //System.out.println(x + " " + j);
                    sample.add(new RGB(source.getRGB(x + i, y + j)));
                }

            }
        }

        int combinedR = 0;
        int combinedG = 0;
        int combinedB = 0;

        //sum of all values on r, g, b channels
        for (RGB rgb : sample) {
            combinedR += rgb.r;
            combinedG += rgb.g;
            combinedB += rgb.b;
        }

        //calculating averages
        combinedR /= sample.size();
        combinedG /= sample.size();
        combinedB /= sample.size();

        return new RGB(combinedR, combinedG, combinedB);
    }

}
