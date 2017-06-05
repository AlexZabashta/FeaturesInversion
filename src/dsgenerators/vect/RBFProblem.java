package dsgenerators.vect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import dsgenerators.EndSearch;
import dsgenerators.ErrorFunction;
import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.dataset.RelationsGenerator;
import jMEF.MultivariateGaussian;
import jMEF.PVector;
import jMEF.PVectorMatrix;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.datagenerators.classifiers.classification.BayesNet;
import weka.datagenerators.classifiers.classification.RandomRBF;

public class RBFProblem implements DoubleProblem {

    final int rep = 10;

    final int a, p, n;
    private final ErrorFunction error;

    public RBFProblem(int a, int p, int n, ErrorFunction error) {
        this.error = error;
        this.a = a;
        this.p = p;
        this.n = n;
    }

    @Override
    public int getNumberOfVariables() {
        return 1;
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

    Random random = new Random();

    @Override
    public void evaluate(DoubleSolution solution) {

        double centrD = solution.getVariableValue(0);

        double avg = 0;

        for (int r = 0; r < rep; r++) {

            int centr = (int) Math.round(centrD + random.nextDouble() - 0.5);

            RandomRBF rbf = new RandomRBF();

            rbf.setRelationName("gen");
            rbf.setNumExamples(2 * (p + n));
            rbf.setNumAttributes(a + 1);
            rbf.setNumCentroids(Math.max(1, centr));
            rbf.setSeed(random.nextInt());

            try {
                rbf.defineDataFormat();
                Instances instances = rbf.generateExamples();
                instances.setClassIndex(a);

                int cntP = 0, cntN = 0;

                for (Instance instance : instances) {

                    if (instance.classValue() < 0.5) {
                        ++cntN;
                    } else {
                        ++cntP;
                    }
                }

                if (cntN == 0) {
                    ++cntN;
                }

                if (cntP == 0) {
                    ++cntP;
                }

                double[][] pos = new double[cntP][a];
                double[][] neg = new double[cntN][a];
                cntP = cntN = 0;

                for (Instance instance : instances) {
                    double[] vec;

                    if (instance.classValue() < 0.5) {
                        vec = neg[cntN++];
                    } else {
                        vec = pos[cntP++];
                    }

                    for (int j = 0; j < a; j++) {
                        vec[j] = instance.value(j);
                    }
                }

                for (; cntP < pos.length; cntP++) {
                    for (int j = 0; j < a; j++) {
                        pos[cntP][j] = random.nextGaussian();
                    }
                }

                for (; cntN < neg.length; cntN++) {
                    for (int j = 0; j < a; j++) {
                        neg[cntN][j] = random.nextGaussian();
                    }
                }

                if (pos.length < neg.length) {
                    double[][] tmp = pos;
                    pos = neg;
                    neg = tmp;
                }
                pos = RelationsGenerator.fit(pos, p, a, random);
                neg = RelationsGenerator.fit(neg, n, a, random);

                avg += error.evaluate(new BinDataset(pos, neg, a));
            } catch (Exception e) {
                if (e instanceof EndSearch) {
                    throw new RuntimeException(e);
                }
                System.err.println(e.getLocalizedMessage());
                avg += 100;
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
        return (double) (1);
    }

    @Override
    public Double getUpperBound(int index) {
        return (double) (32);
    }

}
