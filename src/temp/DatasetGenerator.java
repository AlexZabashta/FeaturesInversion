package temp;

import java.util.List;

import features_inversion.classification.dataset.BinDataset;

public interface DatasetGenerator {
    public List<BinDataset> generate(int numFeatures, int numPosInstances, int numNegInstances, ErrorFunction error, int numDatasets, int limit);
}
