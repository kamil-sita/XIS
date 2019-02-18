package sections.compression;

import java.util.ArrayList;

public class BitSequence {

    private final int BYTE_SIZE = 8; //byte size in bits

    private ArrayList<Boolean> seq = new ArrayList<>();
    int pointer = 0;

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

    public void put(long value, int length) {
        boolean[] arr = toBoolArray(value, length);
        for (boolean b : arr) {
            seq.add(b);
        }
    }

    public void putOne(int value) {
        put(value, 1);
    }

    public boolean has(int size) {
        return seq.size() - pointer >= size;
    }

    public void addAll(BitSequence bitSequence) {
        this.seq.addAll(bitSequence.seq);
    }

    public void resetPointer() {
        pointer = 0;
    }

    public void consume(int size) {
        pointer += size;
        if (pointer > getSizeIgnoreConsumed()) throw new IllegalArgumentException("Consumed more than is available");
    }

    public boolean getAt(int index) {
        return seq.get(index + pointer);
    }

    public int getNext(int count) {
        int out = 0;
        for (int i = 0; i < count; i++) {
            if (seq.get(i + pointer)) {
                int value = 1 << (count - i - 1);
                out = (value | out);
            }
        }
        return out;
    }

    public int getAndConsume(int count) {
        if (!has(count)) consume(count); //throwing error
        int v = getNext(count);
        consume(count);
        return v;
    }

    public ArrayList<Boolean> getSeq() {
        return seq;
    }

    public byte[] getSeqArray() {
        fitByteSize();
        byte[] data = new byte[getSizeIgnoreConsumed() / BYTE_SIZE];
        for (int i = 0; i < data.length; i += 1) {
            data[i] = fitBitsIntoByte(i);
        }
        return data;
    }

    public int getSize() {
        return seq.size() - pointer;
    }

    public int getSizeIgnoreConsumed() {
        return seq.size();
    }

    public int getConsumedSize() {
        return pointer;
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
        int j = 7;
        for (int i = octaId * 8; i < (octaId + 1) * 8; i++) {
            if (seq.get(i)) {
                int value = 1 << j;
                out = (byte) (value | out);
            }
            j--;
        }
        return out;
    }

}