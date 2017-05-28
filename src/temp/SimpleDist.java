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

    @Override
    public double evaluate(BinDataset dataset) {
        double dist = 0;

        for (int i = 0; i < length; i++) {
            double diff = (target[i] - dataset.getMetaFeature(mfIndices[i])) * weight[i];
            dist += diff * diff;
        }

        return Math.sqrt(dist);
    }

}
