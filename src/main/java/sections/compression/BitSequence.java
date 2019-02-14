package sections.compression;

import java.util.ArrayList;

public class BitSequence {
    
    private final int BYTE_SIZE = 8; //byte size in bits
    
    private ArrayList<Boolean> seq = new ArrayList<>();

    public BitSequence() {

    }

    public BitSequence(byte[] data) {
        for (var bytef : data) {
            var bools = toBoolArray(bytef, BYTE_SIZE);
            for (var bool : bools) {
                seq.add(bool);
            }
        }
    }

    public void put(long a, int length) {
        boolean[] arr = toBoolArray(a, length);
        for (boolean b : arr) {
            seq.add(b);
        }
    }

    public ArrayList<Boolean> getSeq() {
        return seq;
    }

    public byte[] getSeqArray() {
        fitByteSize();
        byte[] data = new byte[getSize()/BYTE_SIZE];
        for (int i = 0; i < data.length; i += 1) {
            data[i] = fitBitsIntoByte(i);
        }
        return data;
    }

    public int getSize() {
        return seq.size();
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

    private void fitByteSize() {
        while (getSize() % BYTE_SIZE != 0) {
            put(0, 1);
        }
    }

    private byte fitBitsIntoByte(int octaId) {
        byte out = 0;
        int j = 8;
        for (int i = octaId * 8; i < (octaId + 1) * 8; i++) {
            if (seq.get(i)) {
                int xor = 1 << j;
                out = (byte) (xor ^ out);
            }
            j--;
        }
        return out;
    }
}