package optimization;

import java.util.Random;

public abstract class Crossover<T> {

	public final Random random;

	public Crossover() {
		this(new Random());
	}

	public Crossover(Random random) {
		this.random = random;
	}

	public abstract T cross(T sourceX, T sourceY, Random random);

	public T cross(T sourceX, T sourceY) {
		return cross(sourceX, sourceY, random);
	}

}
