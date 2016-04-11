package optimization;

import java.util.Random;

public abstract class Mutation<T> {

	public final Random random;

	public Mutation() {
		this(new Random());
	}

	public Mutation(Random random) {
		this.random = random;
	}

	public abstract T mutate(T source, Random random);

	public T mutate(T source) {
		return mutate(source, random);
	}

}
