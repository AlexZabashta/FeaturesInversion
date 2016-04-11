package optimization.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import optimization.Measurable;
import distribution.Noramalization;

public class RouletteWheel<T extends Measurable> extends TransformStep<T> {

	public final Noramalization norm;

	public RouletteWheel(Noramalization norm, Random random) {
		super(random);
		this.norm = norm;
	}

	public RouletteWheel(Noramalization norm) {
		super();
		this.norm = norm;
	}

	@Override
	public List<T> perfom(List<T> list, int m, Random random) {
		int n = list.size();

		if (m < 0) {
			throw new RuntimeException("Can't select " + m + " negative number of objects");
		}

		if (m > n) {
			throw new RuntimeException("Can't select " + m + " objects more than list size = " + n);
		}

		List<T> result = new ArrayList<T>(m);

		if (m == n) {
			result.addAll(list);
			return result;
		}

		double[] distribution = new double[n];

		for (int i = 0; i < n; i++) {
			distribution[i] = list.get(i).fitnessFunction();
		}

		norm.normalize(distribution, 0.001);

		double sum = 1;

		for (int i = 0; i < m; i++) {
			double val = random.nextDouble() * sum;

			for (int j = 0; j < n; j++) {
				if (Double.isNaN(distribution[j])) {
					continue;
				}
				val -= distribution[j];
				if (val < 1e-9) {
					result.add(list.get(j));
					sum -= distribution[j];
					distribution[j] = Double.NaN;
					break;
				}
			}

		}

		return result;
	}
}
