package XIS.toolset.imagetools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import static XIS.toolset.imagetools.IntArgb.*;

public class BufferedImageLayers {
    public static BufferedImage divide(BufferedImage image0, BufferedImage image1) {
        if (image0.getType() != BufferedImage.TYPE_INT_ARGB) throw new IllegalArgumentException("Type of BufferedImage must be TYPE_INT_ARGB");
        if (image1.getType() != BufferedImage.TYPE_INT_ARGB) throw new IllegalArgumentException("Type of BufferedImage must be TYPE_INT_ARGB");


        int[] opImage0 = ((DataBufferInt) image0.getRaster().getDataBuffer()).getData();
        int[] opImage1 = ((DataBufferInt) image1.getRaster().getDataBuffer()).getData();
        var output = cloneCanvas(image0);
        int[] outputOp = ((DataBufferInt) output.getRaster().getDataBuffer()).getData();
        System.arraycopy(opImage0, 0, outputOp, 0, opImage0.length);

        int[] argb0 = new int[4];
        int[] argb1 = new int[4];
        int[] argbOut = new int[4];

        for (int i = 0; i < opImage0.length; i++) {
            asArray(opImage0[i], argb0);
            asArray(opImage1[i], argb1);
            argbOut[R] = Math.min(255, (int) (255.0 * argb0[R] / argb1[R]));
            argbOut[G] = Math.min(255, (int) (255.0 * argb0[G] / argb1[G]));
            argbOut[B] = Math.min(255, (int) (255.0 * argb0[B] / argb1[B]));
            outputOp[i] = toRgbaInteger(argbOut);
        }

        return output;
    }

    public static BufferedImage copyImage(BufferedImage input) {
        if (input == null) return null;
        BufferedImage imageCopy = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
        Graphics2D g = imageCopy.createGraphics();
        g.drawImage(input, 0, 0, null);
        g.dispose();
        return imageCopy;
    }

    private static BufferedImage cloneCanvas(BufferedImage input) {
        return new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }
}
