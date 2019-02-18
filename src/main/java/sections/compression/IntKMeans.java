package sections.compression;

import pl.ksitarski.simplekmeans.KMeansData;

import java.util.List;

class IntKMeans implements KMeansData {

    private int val;

    public IntKMeans(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    @Override
    public double distanceTo(KMeansData kMeansData) {
        return Math.abs(val - ((IntKMeans)kMeansData).getVal());
    }

    @Override
    public KMeansData meanOfList(List<KMeansData> list) {
        int a = 0;
        for (var val : list) {
            a += ((IntKMeans) val).getVal();
        }
        a = a / list.size();
        return new IntKMeans(a);
    }
}
