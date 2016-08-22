package optimization.gen;

import java.util.ArrayList;
import java.util.List;

public class ParallelRunnner implements Runnable {
    final List<Runnable> tasks = new ArrayList<Runnable>();

    public void add(Runnable runnable) {
        tasks.add(runnable);
    }

    @Override
    public void run() {
        List<Thread> threads = new ArrayList<Thread>(tasks.size());

        for (Runnable runnable : tasks) {
            threads.add(new Thread(runnable));
        }

        tasks.clear();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
