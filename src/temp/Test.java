package temp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.ifmo.recommendersystem.utils.StatisticalUtils;

import features_inversion.classification.dataset.mf.MetaFeatures;
import temp.aggr.Kurt;
import temp.aggr.Skewn;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.datagenerators.classifiers.classification.Agrawal;
import weka.datagenerators.classifiers.classification.BayesNet;
import weka.datagenerators.classifiers.classification.LED24;
import weka.datagenerators.classifiers.classification.RDG1;
import weka.datagenerators.classifiers.classification.RandomRBF;

public class Test {

    public static void main(String[] args) throws Exception {

        Agrawal g1 = new Agrawal();
        BayesNet g2 = new BayesNet();
        LED24 g3 = new LED24();
        RandomRBF g4 = new RandomRBF();
        RDG1 g5 = new RDG1();

        int n = 20, m = 23;
        ArrayList<Attribute> attributes = new ArrayList<>(m + 1);
        for (int i = 0; i < m; i++) {
            attributes.add(new Attribute("atr" + i));
        }
        attributes.add(new Attribute("class", Arrays.asList("n", "p")));
        Instances format = new Instances("gen", attributes, 0);
        format.setClassIndex(m);

        // g2.setDatasetFormat(format);
        g2.setRelationName("gen");
        g2.setNumExamples(n);
        g2.setNumAttributes(m + 1);
        g2.setNumArcs(m); // m <= m *(m-1)/2
        g2.setCardinality(2);

        g4.setDatasetFormat(format);
        g4.setNumClasses(2);
        g4.setNumExamples(n);
        g4.setNumAttributes(m + 1);
        g4.setNumCentroids(2);
        g4.defineDataFormat();

        g5.setDatasetFormat(format);
        g5.setNumExamples(n);
        g5.setNumAttributes(m + 1);
        g5.setNumNumeric(m + 1);

        g5.setMaxRuleSize(21);
        g5.setMinRuleSize(23);
        g5.setVoteFlag(false);
        
        g5.setNumIrrelevant(0);

        g5.defineDataFormat();
        System.out.println(g5.generateExamples());

    }

    static void testStat() {
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
