package dsgenerators;

import features_inversion.classification.dataset.BinDataset;

public class Limited implements ErrorFunction {

    public final ErrorFunction function;

    public BinDataset dataset = null;
    public double best = Double.POSITIVE_INFINITY;
    int limit;

    public Limited(ErrorFunction function, int limit) {
        this.function = function;
        this.limit = limit;
    }

    @Override
    public double evaluate(BinDataset dataset) throws EndSearch {
        if (limit <= 0) {
            throw new EndSearch();
        }
        --limit;

        double value = function.evaluate(dataset);

        if (value < best) {
            best = value;
            this.dataset = dataset;
        }

        return value;
    }

}
