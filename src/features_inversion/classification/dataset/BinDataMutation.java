package features_inversion.classification.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import features_inversion.util.BooleanArray;
import features_inversion.util.FeaturePoint;
import features_inversion.util.MetaFeaturesExtractor;
import optimization.Crossover;
import optimization.Mutation;

public class BinDataMutation implements Mutation<FeaturePoint<BinDataset>> {
    public final MetaFeaturesExtractor<BinDataset> extractor;

    public BinDataMutation(MetaFeaturesExtractor<BinDataset> extractor) {
        this.extractor = extractor;
    }

    static int randomInt(Random random, int x, int y) {
        return Math.min(x, y) + random.nextInt(Math.abs(x - y) + 1);
    }

    static int randomInt(Random random, int mean) {
        int std = (mean + 1) / 2;
        int val = (int) Math.round((random.nextGaussian() * std + mean));

        if (val < mean - std) {
            val = mean - std;
        }

        if (val < 1) {
            val = 1;
        }
        return val;
    }

    @Override
    public List<FeaturePoint<BinDataset>> mutate(FeaturePoint<BinDataset> source, Random random) {
        BinDataset dataset = source.object;
        List<FeaturePoint<BinDataset>> res = new ArrayList<FeaturePoint<BinDataset>>(1);

        double[][] posA = dataset.pos, negA = dataset.neg, posB, negB;
        int attrA = dataset.numAttr, attrB;

        if (random.nextBoolean()) {
            attrB = attrA;
            posB = RelationsGenerator.fit(posA, randomInt(random, posA.length), attrB, random);
            negB = RelationsGenerator.fit(negA, randomInt(random, negA.length), attrB, random);
        } else {
            // TODO add/remove attr

            posB = negB = null;
            attrB = 0;
        }

        try {
            res.add(new FeaturePoint<BinDataset>(source, new BinDataset(posB, negB, attrB), extractor));
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }

        return res;
    }

}
