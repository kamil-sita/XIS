package sections.scannerToNote;

import universal.tools.imagetools.bufferedimagetools.HSB;
import universal.tools.imagetools.bufferedimagetools.RGB;

public class KMeans {

    private int kMeansPointCount;

    private RGBList kMeansPoints;
    private RGBList points;

    public KMeans (int kMeansPointCount, RGBList points) {
        this.kMeansPointCount = kMeansPointCount;
        this.points = points;
        initialize();
    }

    private void initialize() {
        kMeansPoints = new RGBList();
        for (int i = 0; i < kMeansPointCount; i++) {
            RGB rgb = new RGB();
            initializePointWithRandomValues(rgb);
            kMeansPoints.addRgb(rgb);
        }
    }

    public void iteration() {
        RGBList[] pointsClosestToKMeansPoints = new RGBList[kMeansPointCount];
        for (int i = 0; i < kMeansPointCount; i++) {
            pointsClosestToKMeansPoints[i] = new RGBList();
        }

        for (int i = 0; i < points.getList().size(); i++) {
            RGB closestKMeanPoint = kMeansPoints.getClosestTo(points.getAt(i));
            int idOfKMeanPoint = kMeansPoints.getIdOf(closestKMeanPoint);
            pointsClosestToKMeansPoints[idOfKMeanPoint].addRgb(points.getAt(i));
        }

        kMeansPoints = new RGBList();

        for (int i = 0; i < kMeansPointCount; i++) {
            RGB point = pointsClosestToKMeansPoints[i].getAverage();
            if (point == null) {
                point = new RGB();
                initializePointWithRandomValues(point);
            }
            kMeansPoints.addRgb(point);
        }
    }

    public RGBList getkMeansPoints() {
        return kMeansPoints;
    }

    private void initializePointWithRandomValues(RGB rgb) {
        rgb.r = randomChannelValue();
        rgb.g = randomChannelValue();
        rgb.b = randomChannelValue();
    }

    private int randomChannelValue() {
        return (int) (Math.random() * 255);
    }

}
