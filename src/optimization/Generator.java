package optimization;

import java.util.Random;

public abstract class Generator<T> {

	public final Random random;

	public Generator() {
		this(new Random());
	}

	public Generator(Random random) {
		this.random = random;
	}

	public abstract T generate(Random random);

	public T generate() {
		return generate(random);
	}
}
