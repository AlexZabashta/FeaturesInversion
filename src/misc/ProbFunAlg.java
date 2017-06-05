package misc;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;

import dsgenerators.ErrorFunction;
import dsgenerators.Limited;

public class ProbFunAlg {
    public Problem<?> problem;
    public Limited function;
    public Algorithm<?> algorithm;
    public String file;

    public ProbFunAlg(Problem<?> problem, Limited function) {
        this.problem = problem;
        this.function = function;
    }
}
