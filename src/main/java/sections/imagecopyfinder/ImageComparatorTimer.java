package sections.imagecopyfinder;

class ImageComparatorTimer {
    private long timeStart = System.nanoTime();
    private int length;
    
    public ImageComparatorTimer(int length) {
        this.length = length;
    }


    double getApproximateTimeLeftComparing(int i) {
        //calculating estimated time left
        //if you were to plot comparisons for x-th iteration, formula would go somewhat like this: f(x) = size - x
        //calculating integral of it would be too boring, so instead we will calculate
        //two areas of rectangles

        //rectangle 1
        int base1 = i;
        int height1 = (2 * length - i)/2;

        //rectangle 2
        int base2 = length - i;
        int height2 = (length - i)/2;

        int area1 = base1 * height1;
        int area2 = base2 * height2;

        double dt = System.nanoTime() - timeStart;
        dt = dt * area2/area1;
        dt /= 1000000000;
        return dt;
    }
}