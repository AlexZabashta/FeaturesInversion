import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import distribution.ReRankViaSort;
import distribution.UniformNorm;
import optimization.Crossover;
import optimization.Measurable;
import optimization.Mutation;
import optimization.gen.MutationStep;
import optimization.gen.PairwiseCrossover;
import optimization.gen.RouletteWheel;

public class Test {
	public static void main(String[] args) {
		List<Measurable> list = new ArrayList<Measurable>();

		int n = 40;

		int[] d = new int[n];

		for (int i = 0; i < n; i++) {
			final int num = i;

			list.add(new Measurable() {

				@Override
				public double fitnessFunction() {
					return num * num;
				}

				@Override
				public String toString() {
					return Integer.toString(num);
				}
			});
		}

		Mutation<Measurable> mutation = new Mutation<Measurable>() {
			@Override
			public Measurable mutate(Measurable source, Random random) {
				return source;
			}
		};

		// RouletteWheel<Measurable> wheel = new RouletteWheel<Measurable>(new
		// UniformNorm());
		//
		// for (int rep = 0; rep < 100; rep++) {
		// for (Measurable val : wheel.perfom(list, 20)) {
		// ++d[Integer.parseInt(val.toString())];
		// }
		// }
		//
		// for (int val : d) {
		// System.out.println(val);
		//
		// }

		Crossover<Measurable> crossover = new Crossover<Measurable>() {

			@Override
			public Measurable cross(Measurable sourceX, Measurable sourceY, Random random) {
				if (random.nextBoolean()) {
					return sourceX;
				} else {
					return sourceY;
				}
			}
		};

		PairwiseCrossover<Measurable> pairwiseCrossover = new PairwiseCrossover<>(crossover);
		n /= 2;
		for (int m = 0; m <= n; m++) {
			List<Measurable> res = pairwiseCrossover.perfom(list, m);
			System.out.println(res);
		}

	}
}
