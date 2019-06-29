package XIS.sections.compression;

import java.util.HashMap;
import java.util.Map;

public class ClustererProbabilities {

    private short[] input;
    private Map<Short, Short> probability;
    private short min;
    private short max;

    private short[] originalFunction;

    public ClustererProbabilities(short[] input) {
        if (input == null) throw new IllegalArgumentException("input cannot be null");
        this.input = input;
        min = input[0];
        max = input[0];

        calculate();
    }

    private void calculate() {
        calculateProbabilities();

    }

    private void calculateProbabilities() {
        probability = new HashMap<>();

        for (short value : input) {
            short count = probability.getOrDefault(value, (short) 0);
            probability.put(value, (short) (count + 1));

            if (value < min) min = value;
            if (value > max) max = value;
        }
    }

    private void createFunction() {
        originalFunction = new short[max - min + 1];

        for (int i = 0; i < originalFunction.length; i++) {
            short x = (short) (min + i);
            short y = probability.getOrDefault(x, (short) 0);
            originalFunction[i] = y;
        }
    }

    public short[] getOriginalFunction() {
        return originalFunction;
    }

    public short getMin() {
        return min;
    }

    public short getMax() {
        return max;
    }
}
