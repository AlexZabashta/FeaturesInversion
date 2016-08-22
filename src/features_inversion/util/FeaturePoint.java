package features_inversion.util;

import optimization.Measurable;

public class FeaturePoint<T> extends Point implements Measurable {

    public final Point target, scale;

    private final double fitnessFunction;
    public final T object;

    public FeaturePoint(FeaturePoint<T> copy, T object, MetaFeaturesExtractor<T> extractor) throws Exception {
        this(copy.target, copy.scale, object, extractor);
    }

    public FeaturePoint(Point target, Point scale, T object, MetaFeaturesExtractor<T> extractor) throws Exception {
        super(extractor.extract(object));
        this.target = target;
        this.scale = scale;
        this.object = object;

        double sumOfSquares = 0;

        int d = dimension();

        if (scale.dimension() != d || target.dimension() != d) {
            throw new IllegalArgumentException("Point has different dimension");
        }

        for (int i = 0; i < d; i++) {
            double diff = (super.coordinate(i) - target.coordinate(i)) / scale.coordinate(i);
            sumOfSquares += diff * diff;
        }
        this.fitnessFunction = -Math.sqrt(sumOfSquares);
    }

    @Override
    public double fitnessFunction() {
        return fitnessFunction;
    }

}
