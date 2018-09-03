package universal.tools.imagetools;

import java.awt.image.BufferedImage;
import java.util.List;

public class BufferedImagePalette {

    //TODO: fix
    public static void scaleBrightness(BufferedImage input, List<RGB> rgbList) {

        //double difference = palette.getScaleDifferentiation();
        //double multiplication = palette.getScaleMultiplication();

        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                RGB rgb = new RGB(input.getRGB(x, y));
                HSB hsb = rgb.toHSB();
                //hsb.B -= difference;
                //hsb.B *= multiplication;
                input.setRGB(x, y, hsb.toRGB().toInt());
            }
        }
    }

    public static void replace(BufferedImage input, List<RGB> rgbList) {
        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                RGB originalRgb = new RGB(input.getRGB(x, y));
                RGB rgbToReplaceWith;
                rgbToReplaceWith = getClosestToFromList(rgbList, originalRgb);
                input.setRGB(x, y, rgbToReplaceWith.toInt());
            }
        }
    }

    private static RGB getClosestToFromList(List<RGB> rgbList, RGB target) {
        RGB closest = null;
        double distance = 0;
        for (int i = 0; i < rgbList.size(); i++) {
            RGB rgb = rgbList.get(i);
            if (closest == null || target.getDistanceFrom(rgb) < distance) {
                closest = rgb;
                distance = target.getDistanceFrom(rgb);
            }
        }
        return closest;
    }
}
