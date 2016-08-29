package experiments;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import features_inversion.classification.dataset.BinDataCross;
import features_inversion.classification.dataset.BinDataMutation;
import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.dataset.DAMetaFeatureExtractor;
import features_inversion.classification.dataset.MetaFeatureExtractorsCollection;
import features_inversion.util.FeaturePoint;
import features_inversion.util.Point;
import optimization.Crossover;
import optimization.Mutation;
import optimization.gen.GeneticSearch;
import optimization.result.FunEvalLimit;
import optimization.result.Result;
import optimization.result.StoppingCriterion;
import weka.core.Instances;

public class TestExp {
    public static void main(String[] args) {

        File dataFolder = new File("data" + File.separator + "carff");
        int fx = 33, fy = 38;

        List<MetaFeatureExtractor> all = MetaFeatureExtractorsCollection.all();
        List<MetaFeatureExtractor> extractors = new ArrayList<>();
        extractors.add(all.get(fx));
        extractors.add(all.get(fy));
        DAMetaFeatureExtractor extractor = new DAMetaFeatureExtractor(extractors);

        List<BinDataset> datasets = new ArrayList<BinDataset>();
        List<Point> points = new ArrayList<Point>();

        for (File arff : dataFolder.listFiles()) {
            try {
                Instances instances = new Instances(new FileReader(arff));
                instances.setClassIndex(instances.numAttributes() - 1);
                BinDataset dataset = BinDataset.fromInstances(instances);

                Point point = new Point(extractor.extract(dataset));

                datasets.add(dataset);
                points.add(point);

            } catch (Exception err) {
                System.out.println(arff + " " + err.getMessage());
            }
        }
        int n = datasets.size();
        int m = extractor.numberOfFeatures();

        double[] sum1 = new double[m];
        double[] sum2 = new double[m];

        for (Point point : points) {
            for (int i = 0; i < m; i++) {
                double val = point.coordinate(i);
                sum1[i] += val;
                sum2[i] += val * val;
            }
        }

        double[] mean = new double[m], std = new double[m];

        double tx = 0.8, ty = 0.8;

        // FeaturePoint<BinDataset> point = new FeaturePoint<BinDataset>(target, scale, object, extractor);

        System.out.println(n);

        Crossover<FeaturePoint<BinDataset>> crossover = new BinDataCross(extractor);
        Mutation<FeaturePoint<BinDataset>> mutation = new BinDataMutation(extractor);

        GeneticSearch<FeaturePoint<BinDataset>> search = new GeneticSearch<FeaturePoint<BinDataset>>(crossover, mutation);

        StoppingCriterion<FeaturePoint<BinDataset>> stoppingCriterion = new FunEvalLimit<>(10000);
        List<FeaturePoint<BinDataset>> start = new ArrayList<>();
        Result<FeaturePoint<BinDataset>> result = search.search(start, 0.5, 0.5, stoppingCriterion, null);
        List<FeaturePoint<BinDataset>> finish = result.instances;

    }
}
