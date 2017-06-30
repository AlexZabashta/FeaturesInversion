package dsgenerators.direct;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.problem.Problem;

import dsgenerators.EndSearch;
import dsgenerators.ErrorFunction;
import features_inversion.classification.dataset.BinDataMutation;
import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.fun.AttributeFunction;
import features_inversion.classification.fun.RandomFunction;

public class GDSProblem implements Problem<BinDataSetSolution> {

    final ErrorFunction errorFunction;
    final List<BinDataset> datasets;
    final Random random = new Random();
    final int a, p, n;

    public GDSProblem(int a, int p, int n, ErrorFunction errorFunction, List<BinDataset> datasets) {
        this.a = a;
        this.p = p;
        this.n = n;
        this.errorFunction = errorFunction;
        this.datasets = datasets;
    }

    @Override
    public int getNumberOfVariables() {
        return 1;
    }

    @Override
    public int getNumberOfObjectives() {
        return errorFunction.length();
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + (datasets == null);
    }

    @Override
    public void evaluate(BinDataSetSolution solution) {
        BinDataset dataset = solution.getVariableValue(0);
        try {
            double[] vector = errorFunction.componentwise(dataset);
            for (int i = 0; i < vector.length; i++) {
                solution.setObjective(i, vector[i]);
            }
        } catch (EndSearch e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BinDataSetSolution createSolution() {
        if (datasets == null) {
            double[][] pos = new double[p][a];
            double[][] neg = new double[n][a];
            for (int j = 0; j < a; j++) {

                int d = random.nextInt(10);

                AttributeFunction fun = RandomFunction.generate(random, j, d);
                BinDataMutation.apply(fun, pos, j, true);
                BinDataMutation.apply(fun, neg, j, false);
            }
            return new BinDataSetSolution(new BinDataset(pos, neg, a));

        } else {
            return new BinDataSetSolution(datasets.get(random.nextInt(datasets.size())));
        }
    }
}
