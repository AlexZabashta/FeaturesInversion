package temp;

import features_inversion.classification.dataset.BinDataset;

public interface ErrorFunction {
    double evaluate(BinDataset dataset);
}
