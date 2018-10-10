package toolset.imagetools;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public final class BufferedImageToFXImage {
    public static Image toFxImage(BufferedImage bi) {
        return SwingFXUtils.toFXImage(bi, null);
    }
}
