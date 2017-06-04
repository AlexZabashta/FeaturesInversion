package temp.mop;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import features_inversion.classification.dataset.BinDataset;
import temp.ErrorFunction;

public class DoubleVectProblem implements DoubleProblem {

    final int a, p, n;
    private final ErrorFunction error;

    double lowerBound = -1;
    double upperBound = +1;

    public DoubleVectProblem(ErrorFunction error, int a, int p, int n) {
        this.error = error;
        this.a = a;
        this.p = p;
        this.n = n;
    }

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
        return getClass().getSimpleName() + error.toString();
    }

    public DoubleSolution build(BinDataset dataset) {
        DoubleSolution solution = new DefaultDoubleSolution(this);

        int index = 0;

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
        solution.setObjective(0, error.evaluate(build(solution)));
    }

    @Override
    public DoubleSolution createSolution() {
        DoubleSolution solution = new DefaultDoubleSolution(this);
        return solution;
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
