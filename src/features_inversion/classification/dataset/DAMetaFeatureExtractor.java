package features_inversion.classification.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import features_inversion.classification.ArffConverter;
import features_inversion.util.MetaFeaturesExtractor;
import weka.core.Instances;

public class DAMetaFeatureExtractor implements MetaFeaturesExtractor<BinDataset> {

    private final List<MetaFeatureExtractor> list = new ArrayList<MetaFeatureExtractor>();
    private final int n;

    public DAMetaFeatureExtractor(List<MetaFeatureExtractor> list) {
        this.list.addAll(list);
        this.n = this.list.size();
    }

    @Override
    public int numberOfFeatures() {
        return n;
    }

    @Override
    public double[] extract(BinDataset object) throws Exception {
        double[] features = new double[n];

        Instances instances = object.WEKAInstances();
        if (instances != null) {
            for (int i = 0; i < n; i++) {
                features[i] = list.get(i).extractValue(instances);
            }
        }
        return features;
    }

}
