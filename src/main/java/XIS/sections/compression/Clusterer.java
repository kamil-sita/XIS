package XIS.sections.compression;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom approximate clustering algorithm for one dimension.
 * Clustering works by assuming that every local maximum point is a potential cluster centre.
 */
public class Clusterer {

    private short[] original;
    private short[] derrivative;

    private List<List<Short>> clusters;
    private double[] clusterCenters;

    int min;
    int max;

    int clusterCount = 0;

    public Clusterer(ClustererProbabilities clustererProbabilities) {
        if (clustererProbabilities == null) throw new IllegalArgumentException("ClustererProbablilities cannot be null");
        original = clustererProbabilities.getOriginalFunction();
        min = clustererProbabilities.getMin();
        max = clustererProbabilities.getMax();
        derrivative = new short[original.length];
    }

    private void run() {
        getDerrivative();
        getClusterCount();
        placeInitialClusters();
    }

    private void getDerrivative() {
        for (int i = 0; i < original.length - 1; i++) {
            derrivative[i] = (short) (original[i] - original[i + 1]);
        }
        derrivative[derrivative.length - 1] = -1;
    }

    private void getClusterCount() {
        for (int i = 0; i < derrivative.length - 1; i++) {
            if (derrivative[i] > 0 && derrivative[i + 1] <= 0) {
                clusterCount++;
            }
        }
    }

    private void placeInitialClusters() {
        clusterCenters = new double[clusterCount];
        int clustedId = 0;
        for (int i = 0; i < derrivative.length - 1; i++) {
            if (derrivative[i] > 0 && derrivative[i + 1] <= 0) {
                clusterCenters[clustedId] = min + i;
                clustedId++;
            }
        }
    }

    private void fitIntoClusters() {
        clusters = new ArrayList<>();
        int x = min;
        for (int clusterId = 0; clusterId < clusterCount; clusterId++) {
            boolean isLast = clusterId + 1 == clusterCount;
            List<Short> cluster = new ArrayList<>();
            if (isLast) {
                while (x < max) {
                    cluster.add((short)x);
                    x++;
                }
                clusters.add(cluster);
            } else {

                double dist = distance(x, clusterCenters[clusterId]);
            }
        }
    }

    private double distance(double v1, double v2) {
        return Math.abs(v1 - v2);
    }

}
