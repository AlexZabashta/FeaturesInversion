package optimization.gen;

import java.util.List;
import java.util.Random;

public abstract class TransformStep<T> {

	public final Random random;

	public TransformStep() {
		this(new Random());
	}

	public TransformStep(Random random) {
		this.random = random;
	}

	public List<T> perfom(List<T> list) {
		return perfom(list, random);
	}

	public List<T> perfom(List<T> list, int size) {
		return perfom(list, size, random);
	}

	public abstract List<T> perfom(List<T> list, int size, Random random);

	public List<T> perfom(List<T> list, Random random) {
		return perfom(list, random.nextInt(list.size() + 1), random);
	}

}
