package optimization.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import optimization.Mutation;
import optimization.Measurable;

public class MutationStep<T extends Measurable> extends TransformStep<T> {

	public final Mutation<T> mutation;

	public MutationStep(Mutation<T> mutation, Random random) {
		super(random);
		this.mutation = mutation;
	}

	public MutationStep(Mutation<T> mutation) {
		super();
		this.mutation = mutation;
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

		for (int i = 0; i < n; i++) {
			if ((n - i) * random.nextDouble() < m) {
				result.add(mutation.mutate(list.get(i), random));
				--m;
			}
		}

		return result;

	}
}
