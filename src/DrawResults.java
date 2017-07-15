import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import dsgenerators.ListMetaFeatures;
import features_inversion.classification.dataset.BinDataset;
import misc.FolderUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class DrawResults {
    // D:\eclipse\FeaturesInversion\result\dsgenerators.RunTwoDataset\1500049894797
    // static String src = "result\\dsgenerators.RunMultCritExp\\1498841833828";
    // static String src = "result\\dsgenerators.RunMultCritExp\\1499696328585";

    static String src = "result\\dsgenerators.RunTwoDataset\\1500049894797";
    static final int[] mfIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
    static final int n = mfIndices.length;

    static Set<String> alg = new TreeSet<>();
    static Set<String> prb = new TreeSet<>();
    static Set<String> aps = new TreeSet<>();

    static Map<String, Color> clr = new HashMap<>();

    static Set<String> dat = new TreeSet<>();
    static Map<String, Double> avg = new TreeMap<String, Double>();

    static File folder = new File(src);

    static void read() throws IOException {
        for (File file : folder.listFiles()) {
            String[] name = file.getName().split("_");

            if (name.length != 4) {
                continue;
            }

            alg.add(name[1]);
            prb.add(name[2]);
            dat.add(name[3]);
            aps.add(name[1] + "_" + name[2]);

        }

    }

    static void setColors() {
        Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.PINK, Color.CYAN, Color.MAGENTA, Color.BLACK };
        int i = 0;
        for (String name : prb) {
            Color c = colors[i++];
            clr.put(name, c);

            System.out.println(name + " " + c);

        }
    }

    static void printAP() throws IOException {

        int w = 2000, h = 400;

        for (String d : dat) {

            Map<String, double[]> plots = new HashMap<>();

            for (String p : prb) {

                double[] bestPlot = null;
                double bestVal = Double.POSITIVE_INFINITY;

                for (String a : alg) {
                    File file = new File(folder + "\\1_" + a + "_" + p + "_" + d);

                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            double min = Double.POSITIVE_INFINITY;

                            double val = Double.parseDouble(reader.readLine().substring(2));

                            if (val < bestVal) {
                                bestVal = val;

                                reader.readLine();
                                String[] f = reader.readLine().substring(2).split(" ");
                                int len = f.length;
                                double[] plot = new double[len];

                                for (int i = 0; i < len; i++) {
                                    double v = Double.parseDouble(f[i]);
                                    if (Double.isFinite(v)) {
                                        min = Math.min(min, Math.log10(v));
                                    }
                                    plot[i] = min;
                                }

                                bestPlot = (plot);
                            }

                        }
                    }

                }
                if (bestPlot != null) {
                    plots.put(p, bestPlot);
                }
            }

            double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;

            for (double[] plot : plots.values()) {
                for (double val : plot) {
                    minY = Math.min(minY, Math.floor(val));
                    maxY = Math.max(maxY, Math.ceil(val));
                }
            }

            double minX = 0, maxX = 5;

            System.out.println(minY + " " + maxY);

            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }

            for (String p : prb) {
                double[] plot = plots.get(p);
                if (plot == null) {
                    continue;
                }

                int rgb = clr.get(p).getRGB();

                Graphics graphics = image.getGraphics();
                graphics.setColor(clr.get(p));

                for (int i = 1; i < plot.length; i++) {
                    int x1 = (int) Math.floor(w * (Math.log10(i + 0) - minX) / (maxX - minX));
                    int x2 = (int) Math.floor(w * (Math.log10(i + 1) - minX) / (maxX - minX));

                    int y1 = (int) Math.floor(h * (plot[i - 1] - minY) / (maxY - minY));
                    int y2 = (int) Math.floor(h * (plot[i - 0] - minY) / (maxY - minY));

                    graphics.drawLine(x1, h - y1 - 1, x2, h - y2 - 1);

                }
            }

            ImageIO.write(image, "png", new File(d + "_" + ((int) minY) + "_" + ((int) maxY) + ".png"));

        }

    }

    public static void main(String[] args) throws IOException {
        read();
        setColors();
        printAP();

        // System.out.println(Math.log(131));

    }

}
