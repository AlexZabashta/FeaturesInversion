package features_inversion.classification.dataset;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class BinDataset {
    public final double[][] pos, neg;
    public final int numAttr;

    public BinDataset(double[][] pos, double[][] neg, int numAttr) {
        this.pos = pos;
        this.neg = neg;

        if (numAttr <= 0) {
            throw new IllegalArgumentException("numAttr must be > 0");
        }

        if (pos.length == 0 || neg.length == 0) {
            throw new IllegalArgumentException("'pos' and 'neg' length must be > 0");
        }

        for (double[] inst : neg) {
            if (inst.length < numAttr) {
                throw new IllegalStateException("Some instnce contains < numAttr");
            }
        }

        for (double[] inst : pos) {
            if (inst.length < numAttr) {
                throw new IllegalStateException("Some instnce contains < numAttr");
            }
        }

        this.numAttr = numAttr;
    }

    public static BinDataset fromInstances(Instances instances) {
        int numAttr = instances.numAttributes() - 1;

        int p = 0, n = 0;

        int len = instances.numInstances();

        for (int i = 0; i < len; i++) {
            if (instances.get(i).classValue() < 0.5) {
                ++n;
            } else {
                ++p;
            }
        }

        double[][] pos = new double[p][], neg = new double[n][];

        n = p = 0;

        for (int i = 0; i < len; i++) {

            Instance instance = instances.get(i);
            double[] vec = new double[numAttr];

            for (int j = 0; j < numAttr; j++) {
                vec[j] = instance.value(j);
            }

            if (instances.get(i).classValue() < 0.5) {
                neg[n++] = vec;
            } else {
                pos[p++] = vec;
            }
        }

        return new BinDataset(pos, neg, numAttr);
    }

    public Instances WEKAInstances() {

        ArrayList<Attribute> attributes = new ArrayList<Attribute>(numAttr + 1);
        for (int i = 0; i < numAttr; i++) {
            attributes.add(new Attribute("attr" + i));
        }
        ArrayList<String> classNames = new ArrayList<String>(2);
        classNames.add("neg");
        classNames.add("pos");
        Attribute classAttr = new Attribute("class", classNames);

        attributes.add(classAttr);

        Instances instances = new Instances("name", attributes, neg.length + pos.length);

        instances.setClassIndex(numAttr);

        for (double[] inst : neg) {
            Instance instance = new DenseInstance(numAttr + 1);
            instance.setDataset(instances);
            for (int i = 0; i < numAttr; i++) {
                instance.setValue(i, inst[i]);
            }
            instance.setClassValue(classNames.get(0));
            instances.add(instance);
        }
        for (double[] inst : pos) {
            Instance instance = new DenseInstance(numAttr + 1);
            instance.setDataset(instances);
            for (int i = 0; i < numAttr; i++) {
                instance.setValue(i, inst[i]);
            }
            instance.setClassValue(classNames.get(1));
            instances.add(instance);
        }

        return instances;
    }

}
