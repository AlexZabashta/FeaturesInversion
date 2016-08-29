package features_inversion.classification.fun;

import java.util.Random;

import features_inversion.classification.fun.leaf.AttributeValue;
import features_inversion.classification.fun.leaf.ClassValue;
import features_inversion.classification.fun.leaf.ConstValue;
import features_inversion.classification.fun.node.Abs;
import features_inversion.classification.fun.node.Mul;
import features_inversion.classification.fun.node.Sin;
import features_inversion.classification.fun.node.Sum;

public class RandomFunction {
    public static AttributeFunction generate(Random random, int attr, int level) {
        double p = random.nextDouble();

        if (level <= 0) {
            p *= 0.2999;
        }

        if (p < 0.1) {
            return new ClassValue();
        }

        if (p < 0.2) {
            return new ConstValue(random.nextGaussian());
        }

        if (p < 0.3) {
            return new AttributeValue(random.nextInt(attr));
        }

        if (p < 0.4) {
            return new Sin(generate(random, attr, level - 1));
        }

        if (p < 0.5) {
            return new Abs(generate(random, attr, level - 1));
        }

        if (p < 0.7) {
            return new Sum(generate(random, attr, level - 1), generate(random, attr, level - 1));
        } else {
            return new Mul(generate(random, attr, level - 1), generate(random, attr, level - 1));
        }

    }
}
