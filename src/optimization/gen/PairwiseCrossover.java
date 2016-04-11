package optimization.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import optimization.Crossover;
import optimization.Measurable;

public class PairwiseCrossover<T extends Measurable> extends TransformStep<T> {

	public final Crossover<T> crossover;

	public PairwiseCrossover(Crossover<T> crossover, Random random) {
		super(random);
		this.crossover = crossover;
	}

	public PairwiseCrossover(Crossover<T> crossover) {
		super();
		this.crossover = crossover;
	}

	@Override
	public List<T> perfom(List<T> list, int m, Random random) {
		int n = list.size() / 2;

		if (m < 0) {
			throw new RuntimeException("Can't select " + m + " negative number of objects");
		}

		if (m > n) {
			throw new RuntimeException("Can't select " + m + " objects more than list size/2 = " + n);
		}

		List<T> result = new ArrayList<T>(m);

		for (int i = 0; i < n; i++) {

			if ((n - i) * random.nextDouble() < m) {
				int u = 2 * i, v = 2 * i + 1;
				result.add(crossover.cross(list.get(u), list.get(v), random));
				--m;
			}
		}
		return result;

	}
}
