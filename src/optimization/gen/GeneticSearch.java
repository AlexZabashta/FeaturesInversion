package optimization.gen;

import java.util.ArrayList;
import java.util.List;

import distribution.UniformNorm;
import optimization.Crossover;
import optimization.Measurable;
import optimization.Mutation;
import optimization.result.Result;
import optimization.result.StoppingCriterion;

public class GeneticSearch<T extends Measurable> {

	final SelectBest<T> selectBest;
	final RouletteWheel<T> rouletteWheel;
	final MutationStep<T> mutationStep;
	final PairwiseCrossover<T> crossoverStep;

	public GeneticSearch(Crossover<T> crossover, Mutation<T> mutation) {
		this.selectBest = new SelectBest<T>();
		this.rouletteWheel = new RouletteWheel<T>(new UniformNorm());
		this.mutationStep = new MutationStep<T>(mutation);
		this.crossoverStep = new PairwiseCrossover<T>(crossover);
	}

	public Result<T> search(List<T> generation, double pc, double pm, StoppingCriterion<T> stoppingCriterion, List<Result<T>> results) {
		int generationSize = generation.size();
		int numFunctionEval = 0;
		int maxIter = 10000;

		int numOfChild = Math.max(generationSize / 2, (int) (generationSize * pc));
		int numOfMut = Math.max(generationSize, (int) (generationSize * pm));

		while (true) {
			Result<T> result = new Result<T>(numFunctionEval, generation);
			if (results != null) {
				results.add(result);
			}

			if (stoppingCriterion.test(result) || (--maxIter <= 0)) {
				return result;
			}

			List<T> newGeneration = new ArrayList<T>();

			List<T> parents = rouletteWheel.perfom(generation, numOfChild * 2);
			List<T> children = crossoverStep.perfom(parents, numOfChild);
			newGeneration.addAll(children);
			newGeneration.addAll(generation);
			List<T> mutants = mutationStep.perfom(newGeneration, numOfMut);
			newGeneration.addAll(mutants);

			generation = selectBest.perfom(newGeneration, generationSize);
		}
	}
}
