package dsgenerators.vect;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import dsgenerators.EndSearch;
import dsgenerators.ErrorFunction;
import features_inversion.classification.dataset.BinDataset;
import jMEF.MultivariateGaussian;
import jMEF.PVector;
import jMEF.PVectorMatrix;

public class GMMProblem implements DoubleProblem {

    final int rep = 10;

    final int a, p, n;
    private final ErrorFunction error;

    double lowerBound = -100;
    double upperBound = +100;

    public GMMProblem(int a, int p, int n, ErrorFunction error) {
        this.error = error;
        this.a = a;
        this.p = p;
        this.n = n;
    }

    @Override
    public int getNumberOfVariables() {
        return 2 * (a * a + a);
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
        return getClass().getSimpleName();
    }

    @Override
    public void evaluate(DoubleSolution solution) {

        int index = 0;

        PVectorMatrix posVM, negVM;
        {
            posVM = new PVectorMatrix(a);
            double[] mu = posVM.v.array;
            for (int i = 0; i < a; i++) {
                mu[i] = solution.getVariableValue(index++);
            }

            double[][] sigma = posVM.M.array;
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    sigma[i][j] = solution.getVariableValue(index++);
                }
            }

            posVM.M = posVM.M.Multiply(posVM.M.Transpose());
        }
        {

            negVM = new PVectorMatrix(a);
            double[] mu = negVM.v.array;
            for (int i = 0; i < a; i++) {
                mu[i] = solution.getVariableValue(index++);
            }
            double[][] sigma = negVM.M.array;
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    sigma[i][j] = solution.getVariableValue(index++);
                }
            }

            negVM.M = negVM.M.Multiply(negVM.M.Transpose());
        }

        MultivariateGaussian gmm = new MultivariateGaussian();
        double avg = 0;

        for (int r = 0; r < rep; r++) {
            double[][] pos = new double[p][];
            double[][] neg = new double[n][];

            for (int i = 0; i < p; i++) {
                pos[i] = gmm.drawRandomPoint(posVM).array;
            }
            for (int i = 0; i < n; i++) {
                neg[i] = gmm.drawRandomPoint(negVM).array;
            }

            try {
                avg += error.evaluate(new BinDataset(pos, neg, a));
            } catch (EndSearch e) {
                throw new RuntimeException(e);
            }
        }

        solution.setObjective(0, avg / rep);
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
