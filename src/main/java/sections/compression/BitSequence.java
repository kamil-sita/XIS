package sections.compression;

import java.util.ArrayList;

class BitSequence {
    private ArrayList<Boolean> seq = new ArrayList<>();


    public void put(long a, int length) {
        boolean[] arr = toBoolArray(a, length);
        for (boolean b : arr) {
            seq.add(b);
        }
    }

    private boolean[] toBoolArray(long input, int length) {
        boolean[] arr = new boolean[length];
        int i = 0;
        for (int j = length - 1; j >= 0; j--) {
            int pow = 1 << i;
            arr[j] = (input & pow) > 0;
            i++;
        }
        return arr;
    }

    public ArrayList<Boolean> getSeq() {
        return seq;
    }
}