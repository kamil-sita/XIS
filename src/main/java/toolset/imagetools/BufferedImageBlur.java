package toolset.imagetools;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class BufferedImageBlur {

    public static BufferedImage simpleBlur(BufferedImage input, Kernel kernel) {

        var convolveOp = new ConvolveOp(kernel);

        return convolveOp.filter(input, null);
    }

    public static Kernel genererateGausianKernel(int size) {
        final double SIGMA = 0.84089642;

        float[] f = new float[size * size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int pos = y * size + x;
                double value = 1 / (2 * Math.PI * SIGMA * SIGMA);
                value *= Math.pow(Math.E, -(Math.pow((size / 2) - x, 2) + Math.pow((size / 2) - y, 2))/(2 * SIGMA * SIGMA));
                f[pos] = (float) value;
            }
        }

        float sum = 0;
        for (int i = 0; i < f.length; i++) {
            sum += f[i];
        }

        for (int i = 0; i < f.length; i++) {
            float fNormalize = f[i];
            fNormalize = fNormalize / sum;
            f[i] = fNormalize;
        }

        return new Kernel(size, size, f);
    }


}
