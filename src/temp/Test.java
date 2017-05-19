package temp;

import java.util.Locale;
import java.util.Random;

import com.ifmo.recommendersystem.utils.StatisticalUtils;

import temp.aggr.Kurt;
import temp.aggr.Skewn;

public class Test {

    public static void main(String[] args) {
        Skewn skewn = new Skewn();
        Kurt kurt = new Kurt();

        Random random = new Random();

        int n = 4;

        double[] array = new double[n];

        for (int i = 0; i < n; i++) {
            array[i] = random.nextGaussian() * (i + 1);

            System.out.printf(Locale.ENGLISH, "%f%n", array[i]);
        }
        System.out.println();

        double[] values = array.clone();
        double mean = StatisticalUtils.mean(values);
        double variance = StatisticalUtils.variance(values, mean);

        System.out.println(StatisticalUtils.centralMoment(values, 4, mean) / Math.pow(variance, 2));
        System.out.println(kurt.aggregate(array));

        System.out.println();
        System.out.println(StatisticalUtils.centralMoment(values, 3, mean) / Math.pow(variance, 1.5));
        System.out.println(skewn.aggregate(array));

    }

}
