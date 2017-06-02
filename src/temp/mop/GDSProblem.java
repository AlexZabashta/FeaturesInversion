package temp.mop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.problem.Problem;

import features_inversion.classification.dataset.BinDataset;
import temp.ErrorFunction;

public class GDSProblem implements Problem<BinDataSetSolution> {

    static int cnt = 0;

    private final ErrorFunction errorFunction;
    private final List<BinDataset> datasets;
    private final Random random = new Random();

    public GDSProblem(ErrorFunction errorFunction, List<BinDataset> datasets) {
        this.errorFunction = errorFunction;
        this.datasets = new ArrayList<>(datasets);
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
        return getClass().getSimpleName() + errorFunction.toString();
    }

    @Override
    public void evaluate(BinDataSetSolution solution) {
        BinDataset dataset = solution.getVariableValue(0);
        solution.setObjective(0, errorFunction.evaluate(dataset));
    }

    @Override
    public BinDataSetSolution createSolution() {
        ++cnt;
        return new BinDataSetSolution(datasets.get(random.nextInt(datasets.size())));
    }

}
