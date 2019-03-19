package XIS.toolset.imagetools;

import javafx.util.Pair;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BufferedImageColorPalette {

    public static void scaleBrightness(BufferedImage input, List<Rgb> listOfColors) {

        var diffAndMultipleB = getDiffAndMultB(listOfColors);
        var diffAndMultipleS = getDiffAndMultS(listOfColors);

        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                Rgb rgb = new Rgb(input.getRGB(x, y));
                Hsb hsb = rgb.toHSB();
                hsb.S = (hsb.S - diffAndMultipleS.getKey()) * diffAndMultipleS.getValue();
                hsb.B = (hsb.B - diffAndMultipleB.getKey()) * diffAndMultipleB.getValue();
                input.setRGB(x, y, hsb.toRGB().toInt());
            }
        }
    }

    private static Pair<Double, Double> getDiffAndMultB(List<Rgb> rgbList) {
        List<Hsb> hsbList = new ArrayList<>();
        for (var rgb : rgbList) {
            hsbList.add(rgb.toHSB());
        }

        hsbList.sort((Hsb hsb, Hsb hsb1) -> Double.compare(hsb.B, hsb1.B));

        double diff = hsbList.get(0).B;

        double mult = 1/(hsbList.get(hsbList.size() - 1).B - diff);

        return new Pair<>(diff, mult);
    }

    private static Pair<Double, Double> getDiffAndMultS(List<Rgb> rgbList) {
        List<Hsb> hsbList = new ArrayList<>();
        for (var rgb : rgbList) {
            hsbList.add(rgb.toHSB());
        }

        hsbList.sort((Hsb hsb, Hsb hsb1) -> Double.compare(hsb.S, hsb1.S));

        double diff = hsbList.get(0).S;

        double mult = 1/(hsbList.get(hsbList.size() - 1).S - diff);

        return new Pair<>(diff, mult);
    }

    public static void replace(BufferedImage input, List<Rgb> rgbList) {
        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                Rgb originalRgb = new Rgb(input.getRGB(x, y));
                Rgb rgbToReplaceWith;
                rgbToReplaceWith = getClosestToFromList(rgbList, originalRgb);
                input.setRGB(x, y, rgbToReplaceWith.toInt());
            }
        }
    }

    private static Rgb getClosestToFromList(List<Rgb> rgbList, Rgb target) {
        Rgb closest = null;
        double distance = 0;
        for (int i = 0; i < rgbList.size(); i++) {
            Rgb rgb = rgbList.get(i);
            if (closest == null || target.getDistanceFrom(rgb) < distance) {
                closest = rgb;
                distance = target.getDistanceFrom(rgb);
            }
        }
        return closest;
    }

    public static void scaleAndCutoffBrightness(BufferedImage bufferedImage, double cutOff) {

        double diff = cutOff;
        double scale = 1/(1-cutOff);


        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                var rgb = new Rgb(bufferedImage.getRGB(x, y));
                var hsb = rgb.toHSB();
                if (hsb.B <= cutOff) {
                    hsb.B = 0;
                    bufferedImage.setRGB(x, y, hsb.toRGB().toInt());
                } else {
                    hsb.B -= diff;
                    hsb.B *= scale;
                    bufferedImage.setRGB(x, y, hsb.toRGB().toInt());
                }
            }
        }
    }
}
