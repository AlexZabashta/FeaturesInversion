package temp;

import features_inversion.classification.dataset.BinDataset;

public class SimpleDist implements ErrorFunction {

    final int[] mfIndices;
    final double[] target;
    final double[] weight;
    final int length;

    public SimpleDist(double[] target, double[] weight, int[] mfIndices) {
        this.mfIndices = mfIndices.clone();
        this.target = target.clone();
        this.weight = weight.clone();
        this.length = mfIndices.length;

        if (target.length != length || weight.length != length) {
            throw new IllegalArgumentException("The target, weight, and mfIndices should have the same length");
        }

    }

    public double best = Double.POSITIVE_INFINITY;

    @Override
    public double evaluate(BinDataset dataset) {
        double sumOfSquares = 0;

        for (int i = 0; i < length; i++) {
            double diff = (target[i] - dataset.getMetaFeature(mfIndices[i])) * weight[i];
            sumOfSquares += diff * diff;
        }

        double dist = Math.sqrt(sumOfSquares);

        if (dist < best) {
            best = dist;

            System.out.printf("%7.3f    =", dist);

            for (int i = 0; i < length; i++) {
                double diff = (target[i] - dataset.getMetaFeature(mfIndices[i])) * weight[i];
                System.out.printf("  %7.3f", diff * diff);
            }

            System.out.println();
            System.out.flush();

        }

        return dist;
    }

}
