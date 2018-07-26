package universal.tools.imagetools.bufferedimagetools;

import sections.scannerToNote.RGBList;

import java.awt.image.BufferedImage;

public class BufferedImagePalette {

    public static void scaleBrightness(BufferedImage input, RGBList palette) {

        double difference = palette.getScaleDifferentiation();
        double multiplication = palette.getScaleMultiplication();

        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                RGB rgb = new RGB(input.getRGB(x, y));
                HSB hsb = rgb.toHSB();
                hsb.B -= difference;
                hsb.B *= multiplication;
                input.setRGB(x, y, hsb.toRGB().toInt());
            }
        }
    }

    public static void replace(BufferedImage input, RGBList palette) {
        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                RGB originalRgb = new RGB(input.getRGB(x, y));
                RGB rgbToReplaceWith;
                rgbToReplaceWith = palette.getClosestTo(originalRgb);
                input.setRGB(x, y, rgbToReplaceWith.toInt());
            }
        }
    }
}
