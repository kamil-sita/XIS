package XIS.toolset.imagetools;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;

import static java.lang.Math.*;

public class BufferedImageBlur {

    private static HashMap<Integer, Kernel> flyweightLoader = new HashMap<>();

    public static BufferedImage simpleBlur(BufferedImage input, Kernel kernel) {

        var convolveOp = new ConvolveOp(kernel);

        return convolveOp.filter(input, null);
    }

    public static Kernel generateGaussianKernel(int size) {

        if (flyweightLoader.containsKey(size)) {
            return flyweightLoader.get(size);
        }

        final double SIGMA = 0.84089642;

        float[] f = new float[size * size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int pos = y * size + x;
                //just an equation for value of Gaussian kernel in given point
                double value = 1 / (2 * PI * SIGMA * SIGMA);
                double exponentValue = - (pow((size / 2) - x, 2) + pow((size / 2) - y, 2))/(2 * SIGMA * SIGMA);
                value *= pow(E, exponentValue);
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

        Kernel k = new Kernel(size, size, f);
        flyweightLoader.put(size, k);
        return k;
    }


}
