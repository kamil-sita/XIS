package XIS.sections.compression;

import java.util.List;

class IntKMeans {
    public static double distance(int a, int b) {
        return Math.abs(a - b);
    }

    public static int meanOfList(List<Integer> list) {
        int a = 0;
        for (var val : list) {
            a += val;
        }
        a = a / list.size();
        return a;
    }
}
