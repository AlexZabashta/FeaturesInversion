package experiments;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

import features_inversion.classification.dataset.BinDataCross;
import features_inversion.classification.dataset.BinDataMutation;
import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.dataset.DAMetaFeatureExtractor;
import features_inversion.classification.dataset.MetaFeatureExtractorsCollection;
import features_inversion.util.FeaturePoint;
import features_inversion.util.Point;
import misc.FolderUtils;
import optimization.Crossover;
import optimization.Mutation;
import optimization.gen.GeneticSearch;
import optimization.result.FunEvalLimit;
import optimization.result.Result;
import optimization.result.StoppingCriterion;
import weka.core.Instances;

public class DirectMF {

    public static void main(String[] args) throws IOException {

        File dataFolder = new File("data" + File.separator + "carff");

        List<MetaFeatureExtractor> all = MetaFeatureExtractorsCollection.all().subList(0, 53);

        try (PrintWriter out = new PrintWriter(new File(FolderUtils.clearOrCreate() + "list.txt"))) {

            for (File arff : dataFolder.listFiles()) {
                try {
                    Instances instances = new Instances(new FileReader(arff));
                    instances.setClassIndex(instances.numAttributes() - 1);

                    BinDataset dataset = BinDataset.fromInstances(instances);
                    Instances winst = dataset.WEKAInstances();

                    for (MetaFeatureExtractor mfe : all) {
                        double value = mfe.extractValue(instances);
                        out.print(value + " ");
                        System.out.print(value + " ");
                    }
                    System.out.println();

                    for (MetaFeatureExtractor mfe : all) {
                        double value = mfe.extractValue(winst);
                        out.print(value + " ");
                        System.out.print(value + " ");
                    }
                    System.out.println();
                    System.out.println();

                    // BinDataset dataset = BinDataset.fromInstances(instances);
                    //
                    // Point point = new Point(extractor.extract(dataset));
                    //
                    // datasets.add(dataset);
                    // points.add(point);

                } catch (Exception err) {
                    out.print(err.getMessage());
                    System.out.println(err.getMessage());

                    System.err.println(arff + " " + err.getMessage());
                }
                out.println();

            }
        }

    }
}
