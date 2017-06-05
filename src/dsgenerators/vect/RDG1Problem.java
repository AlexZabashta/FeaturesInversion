package dsgenerators.vect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import dsgenerators.ErrorFunction;
import features_inversion.classification.dataset.BinDataset;
import jMEF.MultivariateGaussian;
import jMEF.PVector;
import jMEF.PVectorMatrix;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.datagenerators.classifiers.classification.BayesNet;
import weka.datagenerators.classifiers.classification.RDG1;
import weka.datagenerators.classifiers.classification.RandomRBF;

public class RDG1Problem implements DoubleProblem {

    final int rep = 10;

    final int a, p, n;
    private final ErrorFunction error;

    public RDG1Problem(ErrorFunction error, int a, int p, int n) {
        this.error = error;
        this.a = a;
        this.p = p;
        this.n = n;
    }

    @Override
    public int getNumberOfVariables() {
        return 4;
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

    Random random = new Random();

    static int rep(int l, int m, int r) {
        return Math.max(l, Math.min(m, r));
    }

    @Override
    public void evaluate(DoubleSolution solution) {

        double rNum = solution.getVariableValue(0);
        double rMax = solution.getVariableValue(1);
        double rMin = solution.getVariableValue(2);
        double rIrr = solution.getVariableValue(3);

        double avg = 0;

        for (int r = 0; r < rep; r++) {

            int m = a + 1;

            RDG1 g5 = new RDG1();
            g5.setNumExamples(p + n);
            g5.setNumAttributes(m);

            g5.setNumNumeric(rep(0, (int) Math.round(m * rNum), m));
            g5.setMaxRuleSize(rep(1, (int) Math.round(m * rMax), m));

            int nMin = rep(1, (int) Math.round(m * rMin), m);
            int nRem = m - nMin;
            int nIrr = rep(0, (int) Math.round(nRem * rIrr), nRem);

            g5.setMinRuleSize(nMin);
            g5.setNumIrrelevant(nIrr);
            g5.setRelationName("gen");

            g5.setSeed(random.nextInt());

            try {
                g5.defineDataFormat();
                Instances instances = g5.generateExamples();

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

                avg += error.evaluate(new BinDataset(pos, neg, a));
            } catch (Exception e) {
                System.err.printf("%d %d %d %d%n", g5.getMinRuleSize(), g5.getMaxRuleSize(), g5.getNumIrrelevant(), g5.getNumNumeric());
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
        return 0.0;
    }

    @Override
    public Double getUpperBound(int index) {
        return 1.0;
    }

}
