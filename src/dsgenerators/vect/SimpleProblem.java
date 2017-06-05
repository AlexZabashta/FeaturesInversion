package dsgenerators.vect;

import java.util.List;
import java.util.Random;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import dsgenerators.EndSearch;
import dsgenerators.ErrorFunction;
import dsgenerators.direct.BinDataSetSolution;
import features_inversion.classification.dataset.BinDataset;

public class SimpleProblem implements DoubleProblem {

    final ErrorFunction error;
    final List<BinDataset> datasets;
    final Random random = new Random();
    final int a, p, n;

    public SimpleProblem(int a, int p, int n, ErrorFunction error, List<BinDataset> datasets) {
        this.a = a;
        this.p = p;
        this.n = n;
        this.error = error;
        this.datasets = datasets;
    }

    final static double lowerBound = -2;
    final static double upperBound = +2;

    @Override
    public int getNumberOfVariables() {
        return a * (p + n);
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + (datasets == null);
    }

    public DoubleSolution build(BinDataset dataset) {
        DoubleSolution solution = new DefaultDoubleSolution(this);

        int index = 0;

        int a = Math.min(this.a, dataset.numAttr);
        int p = Math.min(this.p, dataset.pos.length);
        int n = Math.min(this.n, dataset.neg.length);

        for (int i = 0; i < p; i++) {
            for (int j = 0; j < a; j++) {
                solution.setVariableValue(index++, dataset.pos[i][j]);
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < a; j++) {
                solution.setVariableValue(index++, dataset.neg[i][j]);
            }
        }

        return solution;
    }

    public BinDataset build(DoubleSolution solution) {
        double[][] pos = new double[p][a];
        double[][] neg = new double[n][a];

        int index = 0;

        for (int i = 0; i < p; i++) {
            for (int j = 0; j < a; j++) {
                pos[i][j] = solution.getVariableValue(index++);
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < a; j++) {
                neg[i][j] = solution.getVariableValue(index++);
            }
        }

        return new BinDataset(pos, neg, a);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        try {
            solution.setObjective(0, error.evaluate(build(solution)));
        } catch (EndSearch e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DoubleSolution createSolution() {
        if (datasets == null) {
            return new DefaultDoubleSolution(this);
        } else {
            return build(datasets.get(random.nextInt(datasets.size())));
        }
    }

    @Override
    public Double getLowerBound(int index) {
        return lowerBound;
    }

    @Override
    public Double getUpperBound(int index) {
        return upperBound;
    }

}
