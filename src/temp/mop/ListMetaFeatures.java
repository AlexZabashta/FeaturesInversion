package temp.mop;

import java.util.ArrayList;
import java.util.List;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;
import com.ifmo.recommendersystem.metafeatures.general.DataSetDimensionality;

import features_inversion.classification.dataset.BinDataset;

public class ListMetaFeatures {

    private static final List<MetaFeatureExtractor> mf = new ArrayList<>();

    static {
        mf.add(new DataSetDimensionality());
    }

    public static String getName(int index) {
        return mf.get(index).getName();
    }

    public static double extractValue(int index, BinDataset dataset) {
        try {
            return mf.get(index).extractValue(dataset);
        } catch (Exception e) {
            System.err.println(e.getMessage() + " " + e.getLocalizedMessage());
            return Double.NaN;
        }
    }

}
