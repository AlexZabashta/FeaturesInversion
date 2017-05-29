package temp.mop;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;

import features_inversion.classification.dataset.BinDataset;
import temp.ErrorFunction;
import temp.SimpleDist;

public class TestExp {

    public static void main(String[] args) {

        int[] mfIndices = { 1, 2, 3, 4, 5 };
        int n = mfIndices.length;

        double[] target = new double[n];
        double[] weight = new double[n];

        List<BinDataset> datasets = new ArrayList<BinDataset>();

        ErrorFunction errorFunction = new SimpleDist(target, weight, mfIndices);
        GDSProblem problem = new GDSProblem(errorFunction, datasets);

        CrossoverOperator<BinDataSetSolution> crossoverOperator = null;
        MutationOperator<BinDataSetSolution> mutationOperator = null;

        NSGAIIBuilder<BinDataSetSolution> nsgaiiBuilder = new NSGAIIBuilder<BinDataSetSolution>(problem, crossoverOperator, mutationOperator);
        NSGAII<BinDataSetSolution> ga = nsgaiiBuilder.build();

        ga.getResult();

        // Algorithm<?> a = new

    }

}
