package experiments;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import features_inversion.classification.dataset.BinDataCross;
import features_inversion.classification.dataset.BinDataMutation;
import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.dataset.MetaFeatureExtractorsCollection;
import features_inversion.classification.dataset.DAMetaFeatureExtractor;
import features_inversion.classification.fun.AttributeFunction;
import features_inversion.classification.fun.RandomFunction;
import features_inversion.util.FeaturePoint;
import features_inversion.util.Point;
import misc.FolderUtils;
import optimization.Crossover;
import optimization.Mutation;
import optimization.gen.GeneticSearch;
import optimization.gen.ParallelRunnner;
import optimization.result.FunEvalLimit;
import optimization.result.Result;
import optimization.result.StoppingCriterion;
import weka.core.Instances;

public class MultiDimGrid {

    public static double convert(int c, Point point, Point target, Point scale) {
        double v = (point.coordinate(c) - target.coordinate(c)) / (3 * scale.coordinate(c));
        return v + 0.5;
    }

    public static <T> void print(String path, List<FeaturePoint<T>> points, Point target, Point scale) throws IOException {
        int s = 400;

        int wn = 3;
        int hn = 2;

        int w = wn * s;
        int h = hn * s;

        int pointColor = 0x000000, lineColor = 0x545454;
        int blackColor = 0x000000, whiteColor = 0xFFFFFF;

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
        {
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, whiteColor);
                }
            }
        }

        int dim = target.dimension();

        int index = 0;
        for (int ordinate = 0; ordinate < dim; ordinate++) {
            for (int abscissa = 0; abscissa < ordinate; abscissa++) {

                int offsetX = (index % wn);
                int offsetY = (index / wn);

                offsetX *= s;
                offsetY *= s;

                {
                    int y = 0;
                    for (int x = 0; x < s; x++) {
                        image.setRGB(x + offsetX, y + offsetY, blackColor);
                    }
                }

                {
                    int x = 0;
                    for (int y = 0; y < s; y++) {
                        image.setRGB(x + offsetX, y + offsetY, blackColor);
                    }
                }
                {
                    int y = (int) Math.floor(0.5 * s);
                    if (y >= s) {
                        y = s - 1;
                    }
                    if (y < 0) {
                        y = 0;
                    }
                    for (int x = 0; x < s; x++) {
                        image.setRGB(x + offsetX, y + offsetY, lineColor);
                    }
                }

                {
                    int x = (int) Math.floor(0.5 * s);
                    if (x >= s) {
                        x = s - 1;
                    }
                    if (x < 0) {
                        x = 0;
                    }
                    for (int y = 0; y < s; y++) {
                        image.setRGB(x + offsetX, y + offsetY, lineColor);
                    }
                }
                int rad = 3;
                for (FeaturePoint<T> p : points) {
                    if (p == null) {
                        continue;
                    }
                    int px = (int) Math.round(convert(abscissa, p, target, scale) * s);
                    int py = (int) Math.round(convert(ordinate, p, target, scale) * s);

                    for (int dx = -rad; dx <= rad; dx++) {
                        for (int dy = -rad; dy <= rad; dy++) {
                            int ds = Math.abs(dx) + Math.abs(dy);
                            if (ds <= rad) {
                                int x = px + dx;
                                int y = py + dy;
                                if (0 <= x && x < s && 0 <= y && y < s) {
                                    image.setRGB(x + offsetX, y + offsetY, pointColor);
                                }

                            }

                        }
                    }
                }

                index++;
            }
        }

        {
            int x = w - 1;
            for (int y = 0; y < h; y++) {
                image.setRGB(x, y, blackColor);
            }
        }

        {
            int y = h - 1;
            for (int x = 0; x < w; x++) {
                image.setRGB(x, y, blackColor);
            }
        }

        ImageIO.write(image, "png", new File(path + ".png"));
    }

    public static void main(String[] args) throws IOException {

        File dataFolder = new File("data" + File.separator + "carff");

        // List<MetaFeatureExtractor> all = MetaFeatureExtractorsCollection.rat();

        List<MetaFeatureExtractor> extractors = MetaFeatureExtractorsCollection.treestat4();

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

        for (int i = 0; i < m; i++) {
            mean[i] = sum1[i] / n;
            std[i] = Math.sqrt(Math.max(sum2[i] / n - mean[i] * mean[i], 1e-8));
        }

        // for (int i = 0; i < m; i++) {
        // if (i % 2 == 1) {
        // mean[i] *= 2;
        // } else {
        // mean[i] /= 2;
        // }
        // }

        Point target = new Point(mean);
        Point scale = new Point(std);

        List<FeaturePoint<BinDataset>> featurePoints = new ArrayList<FeaturePoint<BinDataset>>();

        for (int i = 0; i < n; i++) {
            try {
                featurePoints.add(new FeaturePoint<BinDataset>(target, scale, datasets.get(i), extractor));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        Collections.sort(featurePoints);

        List<FeaturePoint<BinDataset>> start = new ArrayList<>();

        for (FeaturePoint<BinDataset> point : featurePoints) {
            int close = 0;
            for (int i = 0; i < m; i++) {
                if (point.dist(i) < 0.2) {
                    ++close;
                }
            }

            // System.out.println(close + " / " + m);

            if (close == 0) {
                start.add(point);
            }
        }

        String path = FolderUtils.openOrCreate("treestat4", "1");

        print(path + "init", featurePoints, target, scale);
        // print(path + "start.png", start, target, scale);

        int evalLimit = 6543;

        Crossover<FeaturePoint<BinDataset>> crossover = new BinDataCross(extractor);
        Mutation<FeaturePoint<BinDataset>> mutation = new BinDataMutation(extractor);

        GeneticSearch<FeaturePoint<BinDataset>> search = new GeneticSearch<FeaturePoint<BinDataset>>(crossover, mutation);

        StoppingCriterion<FeaturePoint<BinDataset>> stoppingCriterion = new FunEvalLimit<>(evalLimit);
        List<Result<FeaturePoint<BinDataset>>> results = new ArrayList<>();
        Result<FeaturePoint<BinDataset>> result = search.search(start, 0.5, 0.5, stoppingCriterion, results);
        // List<FeaturePoint<BinDataset>> finish = result.instances;

        int len = results.size();

        for (int i = 0; i < len; i++) {
            print(path + "step" + i, results.get(i).instances, target, scale);
        }

        List<FeaturePoint<BinDataset>> gen = new ArrayList<>();

        int threads = 16;
        List<Runnable> generators = new ArrayList<>();
        int genSize = evalLimit / threads;

        for (int t = 0; t < threads; t++) {
            generators.add(new Runnable() {
                @Override
                public void run() {
                    Random random = new Random();
                    for (int i = 0; i < genSize; i++) {
                        int attr = random.nextInt(200) + 1;
                        int posN = random.nextInt(200) + 1;
                        int negN = random.nextInt(200) + 1;

                        double[][] pos = new double[posN][attr];
                        double[][] neg = new double[negN][attr];

                        for (int j = 0; j < attr; j++) {
                            AttributeFunction fun = RandomFunction.generate(random, j, 4);
                            BinDataMutation.apply(fun, pos, j, true);
                            BinDataMutation.apply(fun, neg, j, false);
                        }

                        BinDataset genDataset = new BinDataset(pos, neg, attr);
                        try {
                            FeaturePoint<BinDataset> point = new FeaturePoint<BinDataset>(target, scale, genDataset, extractor);
                            synchronized (gen) {
                                gen.add(point);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            });
        }

        Random random = new Random(322);

        Result<FeaturePoint<BinDataset>> annealing = null;
        {
            FeaturePoint<BinDataset> cur = null;

            for (FeaturePoint<BinDataset> point : featurePoints) {
                if (cur == null || point.fitnessFunction() > cur.fitnessFunction()) {
                    cur = point;
                }
            }

            double temp = 1;
            double coolingRate = 0.003;

            FeaturePoint<BinDataset> best = cur;

            annealing = new Result<>(genSize, 123, featurePoints);

            for (int iter = 0; iter < evalLimit; iter += 6) {
                List<FeaturePoint<BinDataset>> mutants = mutation.mutate(cur, random);

                if (mutants.isEmpty()) {
                    continue;
                }

                FeaturePoint<BinDataset> mutant = mutants.get(0);

                if (annealing.bestValue < mutant.fitnessFunction()) {
                    annealing = new Result<>(genSize, iter, mutants);
                    print(path + "ann" + iter, mutants, target, scale);
                }

                if (cur.fitnessFunction() < mutant.fitnessFunction()) {
                    cur = mutant;
                    if (cur.fitnessFunction() > best.fitnessFunction()) {
                        print(path + "ann" + iter, mutants, target, scale);
                        best = cur;
                    }
                } else {
                    if (random.nextDouble() < Math.exp((cur.fitnessFunction() - mutant.fitnessFunction()) / temp)) {
                        cur = mutant;
                    }
                }

                // if (newEnergy < energy) {
                // return 1.0;
                // }
                // // If the new solution is worse, calculate an acceptance probability
                // return Math.exp((energy - newEnergy) / temperature);
                //
                temp *= 1 - coolingRate;
            }

        }
        ParallelRunnner.run(generators);

        Result<FeaturePoint<BinDataset>> genData = new Result<>(evalLimit, 0, gen);
        print(path + "gen", gen, target, scale);
        try (PrintWriter log = new PrintWriter((path + "result.txt"))) {
            log.println(result.bestValue);
            log.println(genData.bestValue);
            log.println(annealing.bestValue);
        }

    }
}
