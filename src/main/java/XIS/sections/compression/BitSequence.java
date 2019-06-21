package XIS.sections.compression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Specific collection (list) that is used as a way to make working on data formed right from binary values easier. Resembles FIFO queue.
 * Values are not actually removed, instead pseudo pointer is pointing at corresponding position. If you want to read queue again, you should use resetPointer() method.
 */
public class BitSequence {
    private final int BYTE_SIZE = 8; //byte size in bits

    private List<Boolean> sequence = new ArrayList<>();
    int pointer = 0;

    public BitSequence() {

    }

    public BitSequence(byte[] data) {
        for (var bytef : data) {
            var bools = toBoolArray(bytef, BYTE_SIZE);
            for (var bool : bools) {
                sequence.add(bool);
            }
        }
    }

    public void insert(long value, int length) {
        boolean[] arr = toBoolArray(value, length);
        for (boolean b : arr) {
            sequence.add(b);
        }
    }

    public void insert(boolean value) {
        sequence.add(value);
    }

    public void insert(int value) {
        insert(value, 1);
    }

    public boolean has(int size) {
        return sequence.size() - pointer >= size;
    }

    public void addAll(BitSequence bitSequence) {
        this.sequence.addAll(bitSequence.sequence);
    }

    public void resetPointer() {
        pointer = 0;
    }

    public void movePointer(int size) {
        pointer += size;
        if (pointer > getSize()) throw new IllegalArgumentException("Pointer out of bounds");
    }

    public boolean peekAt(int index) {
        return sequence.get(index + pointer);
    }

    public int peekNext(int count) {
        int out = 0;
        for (int i = 0; i < count; i++) {
            if (sequence.get(i + pointer)) {
                int value = 1 << (count - i - 1);
                out = (value | out);
            }
        }
        return out;
    }

    public int remove(int count) {
        if (!has(count)) movePointer(count);
        int v = peekNext(count);
        movePointer(count);
        return v;
    }

    public List<Boolean> getAllAsList() {
        return sequence;
    }

    public byte[] getAllAsByteArray() {
        fitBytePadding();
        byte[] data = new byte[getSize() / BYTE_SIZE];
        for (int i = 0; i < data.length; i += 1) {
            data[i] = fitBitsIntoByte(i);
        }
        return data;
    }

    public int getSizeLeft() {
        return sequence.size() - pointer;
    }

    public int getSize() {
        return sequence.size();
    }

    public int getPointerPosition() {
        return pointer;
    }

    public void clear() {
        sequence.clear();
        resetPointer();
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

    private void fitBytePadding() {
        while (getSizeLeft() % BYTE_SIZE != 0) {
            insert(0, 1);
        }
    }

    private byte fitBitsIntoByte(int bytePosId) {
        byte out = 0;
        int j = 7;
        for (int i = bytePosId * 8; i < (bytePosId + 1) * 8; i++) {
            if (sequence.get(i)) {
                int value = 1 << j;
                out = (byte) (value | out);
            }
            j--;
        }
        return out;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BitSequence that = (BitSequence) o;
        if (that.sequence.size() != this.sequence.size()) return false;
        for (int i = 0; i < this.sequence.size(); i++) {
            if (!sequence.get(i).equals(that.sequence.get(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence);
    }
}