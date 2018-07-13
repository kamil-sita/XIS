package universal.tools.imagetools.bufferedimagetools;

import java.awt.image.BufferedImage;

public final class BufferedImageScale {


    public static BufferedImage generatePreviewImage (BufferedImage source, final int SIZE) {
        if (source == null) return null;

        int targetWidth;
        int targetHeight;

        if (source.getHeight() > source.getWidth()) {
            targetHeight = SIZE;
            targetWidth = (int) (SIZE * ((source.getWidth() * 1.0)/source.getHeight()));
        } else {
            targetWidth = SIZE;
            targetHeight = (int) (SIZE * ((source.getHeight() * 1.0)/source.getWidth()));
        }

        BufferedImage output = new BufferedImage(targetWidth, targetHeight, source.getType());

        int scaleX = source.getWidth()/targetWidth;
        int scaleY = source.getHeight()/targetHeight;

        for (int x = 0; x < targetWidth; x++) {
            for (int y = 0; y < targetHeight; y++) {

                RGB rgb = getAverageOfArea(x * scaleX, y * scaleY, (x+1) * scaleX, (y+1) * scaleY, source);
                output.setRGB(x, y, rgb.toInt());

            }
        }

        return output;

    }

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

                int color = getAverageRgbOfTile(x, y, source, SIZE).toInt();

                RGB rgb = new RGB(color);

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

        //ArrayList<RGB> sample = new ArrayList<>();
        RGB[] sample = new RGB[sampleHeight*sampleWidth];
        int arrayIndex = 0;

        //Gettting samples from BufferedImage
        for (int i = 0; i < sampleWidth; i++) {
            for (int j = 0; j < sampleHeight; j++) {

                if (x + i < source.getWidth() && y + j < source.getHeight()) {
                    sample[arrayIndex] = new RGB(source.getRGB(x + i, y + j));
                    arrayIndex++;
                }

            }
        }

        int combinedR = 0;
        int combinedG = 0;
        int combinedB = 0;

        //sum of all values on r, g, b channels

        for (int i = 0; i < arrayIndex; i++) {
            RGB rgb = sample[i];
            combinedR += rgb.r;
            combinedG += rgb.g;
            combinedB += rgb.b;
        }

        //calculating averages
        combinedR /= arrayIndex;
        combinedG /= arrayIndex;
        combinedB /= arrayIndex;

        return new RGB(combinedR, combinedG, combinedB);
    }

    private static RGB getAverageOfArea (int xStart, int yStart, int xEnd, int yEnd, BufferedImage source) {
        RGB[] sample = new RGB[(xEnd - xStart) * (yEnd - yStart)];

        int arrayIndex = 0;

        for (int x = xStart; x < xEnd; x++) {
            for (int y = yStart; y < yEnd; y++) {

                if (x < source.getWidth() && y < source.getHeight()) {
                    sample[arrayIndex] = new RGB(source.getRGB(x, y));
                    arrayIndex++;
                }

            }
        }

        int combinedR = 0;
        int combinedG = 0;
        int combinedB = 0;

        for (int i = 0; i < arrayIndex; i++) {
            RGB rgb = sample[i];
            combinedR += rgb.r;
            combinedB += rgb.b;
            combinedG += rgb.g;
        }

        combinedR /= arrayIndex;
        combinedG /= arrayIndex;
        combinedB /= arrayIndex;

        return new RGB(combinedR, combinedG, combinedB);
    }

}
