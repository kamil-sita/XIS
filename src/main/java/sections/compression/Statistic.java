package sections.compression;

import java.util.ArrayList;
import java.util.List;

public class Statistic {
    private int size;
    private int pixels;
    private List<Integer> dictionarySizes = new ArrayList<>();
    private List<Integer> rleLines = new ArrayList<>();
    private List<Integer> differentialLines = new ArrayList<>();
    private List<Integer> normalLines = new ArrayList<>();

    //adders/setters
    public void setSize(int size) {
        this.size = size;
    }

    public void setPixels(int pixels) {
        this.pixels = pixels;
    }

    public void addRleLineSize(int value) {
        rleLines.add(value);
    }

    public void addDifferentialLinesSize(int value) {
        differentialLines.add(value);
    }

    public void addNormalLineSize(int value) {
        normalLines.add(value);
    }

    public void addDictionarySize(int value) {
        dictionarySizes.add(value);
    }

    //getters
    public int getSize() {
        return size;
    }

    public int getPixels() {
        return pixels;
    }

    public int getRleLinesCompressed() {
        return rleLines.size();
    }

    public int getDifferentialLinesCompressed() {
        return differentialLines.size();
    }

    public int getNormallyCompressed() {
        return normalLines.size();
    }

    public double getAverageRleLineSize() {
        double av = 0;
        for (Integer integer : rleLines) {
            av += integer;
        }
        return av/ rleLines.size();
    }

    public double getAverageDifferentialLinesSize() {
        double av = 0;
        for (Integer integer : differentialLines) {
            av += integer;
        }
        return av/ differentialLines.size();
    }

    public double getAverageNormalLineSize() {
        double av = 0;
        for (Integer integer : normalLines) {
            av += integer;
        }
        return av/ normalLines.size();
    }

    public double getAverageDictionarySize() {
        double av = 0;
        for (Integer integer : dictionarySizes) {
            av += integer;
        }
        return av/ dictionarySizes.size();
    }

    @Override
    public String toString() {
        return  "Size (bytes) = " + size +
                "\nPixels= " + pixels +
                "\nAverage dictionary size = " + getAverageDictionarySize() +
                "\nLines compressed by RLE = " + rleLines.size() +
                "\nLines compressed by DIFF = " + differentialLines.size() +
                "\nLines compressed by SIMPLE = " + normalLines.size() +
                "\nAverage length of RLE = " + getAverageRleLineSize() +
                "\nAverage length of DIFF = " + getAverageDifferentialLinesSize() +
                "\nAverage length of SIMPLE = " + getAverageNormalLineSize();
    }
}
