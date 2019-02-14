package sections.compression;

import java.util.ArrayList;

public class BitSequenceDecoder {
    private ArrayList<Boolean> bitSequence;
    private int pos = 0;

    public BitSequenceDecoder(BitSequence bitSequence) {
        this.bitSequence = bitSequence.getSeq();
    }

    public boolean has(int size) {
        return bitSequence.size() - pos >= size;
    }

    public int get(int bits) {
        System.out.println("new number:");
        int ret = 0;
        for (int i = 0; i < bits; i++) {
            ret = ret << 1;
            ret = ret + (get() ? 1 : 0);
        }
        return ret;
    }

    private boolean get() {
        if (pos >= bitSequence.size()) return false;
        boolean v = bitSequence.get(pos);
        pos++;
        return v;
    }

    private boolean peek() {
        return bitSequence.get(pos);
    }


}
