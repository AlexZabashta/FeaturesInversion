package optimization.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import optimization.Measurable;

public class SelectBest<T extends Measurable> extends TransformStep<T> {

	public final Comparator<T> comparator;

	public SelectBest() {
		this(new Random());
	}

	public SelectBest(Comparator<T> comparator) {
		this(comparator, new Random());
	}

	public SelectBest(Random random) {
		this(new Comparator<T>() {
			@Override
			public int compare(T x, T y) {
				return Double.compare(y.fitnessFunction(), x.fitnessFunction());
			}
		}, random);
	}

	public SelectBest(Comparator<T> comparator, Random random) {
		super(random);
		this.comparator = comparator;
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

		List<T> result = new ArrayList<T>(n);
		result.addAll(list);

		Collections.shuffle(result, random);
		Collections.sort(result, comparator);

		return result.subList(0, m);

	}
}
