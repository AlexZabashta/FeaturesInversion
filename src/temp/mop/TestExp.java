package temp.mop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;

import features_inversion.classification.dataset.BinDataset;
import temp.ErrorFunction;
import temp.SimpleDist;
import weka.core.Instances;

public class TestExp {

    public static void main(String[] args) {

        final int[] mfIndices = { 4, 5, 6, 9, 18, 19, 28, 29 };
        final int n = mfIndices.length;

        final double[] sum0 = new double[n];
        final double[] sum1 = new double[n];
        final double[] sum2 = new double[n];

        final List<BinDataset> datasets = new ArrayList<BinDataset>();

        for (File file : new File("data\\bin_arff\\").listFiles()) {
            try (FileReader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);
                instances.setClassIndex(instances.numAttributes() - 1);
                BinDataset dataset = BinDataset.fromInstances(instances);

                boolean nan = false;
                for (int i = 0; i < n; i++) {
                    double val = dataset.getMetaFeature(mfIndices[i]);
                    if (Double.isNaN(val)) {
                        nan = true;
                    } else {
                        sum0[i] += 1;
                        sum1[i] += val;
                        sum2[i] += val * val;
                    }
                }

                if (nan) {
                    System.err.println(file.getName() + " mf is NaN");
                } else {
                    datasets.add(dataset);
                }

            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
        }

        System.out.println(Arrays.toString(sum0));
        System.out.println(Arrays.toString(sum1));
        System.out.println(Arrays.toString(sum2));

        final double[] target = new double[n];
        final double[] weight = new double[n];

        for (int i = 0; i < n; i++) {
            double mX1 = sum1[i] / sum0[i];
            double mX2 = sum2[i] / sum0[i];

            double mean = mX1;
            double var = mX2 - mX1 * mX1;
            double std = Math.sqrt(var);

            target[i] = mean;
            weight[i] = 1 / std;
        }

        System.out.println(Arrays.toString(target));
        System.out.println(Arrays.toString(weight));

        ErrorFunction errorFunction = new SimpleDist(target, weight, mfIndices);

        GenOverObj g = new GenOverObj(datasets);

        long time = System.currentTimeMillis();

        int pop = 50;

        System.out.println(g.generate(-1, -1, -1, errorFunction, pop, pop * 40).size());

        System.out.println("time " + (System.currentTimeMillis() - time));

    }

}
