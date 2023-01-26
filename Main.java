import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static int MIN = 2;
    public static int MAX = (int) Math.pow(10, 8);
    public static int NUM_THREADS = 8;

    public static void main(String[] args) throws InterruptedException {
        boolean primes[] = new boolean[MAX + 1];
        Arrays.fill(primes, true);
        ExecutorService threadExecutor = Executors.newFixedThreadPool(8);
        Metrics metrics = new Metrics();

        metrics.start();
        for (int i = 2; i * i < MAX; i++) {
            // If prime[p] is not changed, then it is a prime
            if (primes[i])
                threadExecutor.execute(new Eratosthenes(i, primes, metrics));
        }

        threadExecutor.shutdown();
        threadExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        metrics.end();
        metrics.calculateNumAndSumPrimes(primes);
        metrics.output();
    }
}

class Eratosthenes implements Runnable {
    private int n;
    private boolean primes[];
    private Metrics metrics;

    public Eratosthenes(int n, boolean primes[], Metrics metrics) {
        this.n = n;
        this.primes = primes;
        this.metrics = metrics;
    }

    @Override
    public void run() {
        // record start time
        long startTime = System.currentTimeMillis();
        int iterations = 0;
        // Update all multiples of p
        for (int i = n * n; i <= Main.MAX; i += n) {
            primes[i] = false;
            iterations++;
        }
        // record end time
        long endTime = System.currentTimeMillis();
        metrics.recordIterationsPerThread(Thread.currentThread().getName(), iterations);
        metrics.recordDurationPerThread(Thread.currentThread().getName(), endTime - startTime);
    }
}

class Metrics {
    public long startTime;
    public long endTime;
    public long numPrimes;
    public long sumPrimes;

    // list of threads and their iterations
    HashMap<String, Integer> threadIterations = new HashMap<String, Integer>();
    HashMap<String, Long> threadDurations = new HashMap<String, Long>();

    public Metrics() {
        numPrimes = 0;
        sumPrimes = 0;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void end() {
        endTime = System.currentTimeMillis();
    }

    public void output() {
        System.out.println("Time: " + (endTime - startTime) + " ms");
        System.out.println("Number of primes: " + numPrimes);
        System.out.println("Sum of primes: " + sumPrimes);

        System.out.println("Iterations per thread: ");
        for (String threadName : threadIterations.keySet()) {
            System.out.println(threadName + ": " + threadIterations.get(threadName));
        }

        System.out.println("Time per thread: ");
        for (String threadName : threadDurations.keySet()) {
            System.out.println(threadName + ": " + threadDurations.get(threadName));
        }
    }

    public void calculateNumAndSumPrimes(boolean primes[]) {
        for (int i = 2; i < primes.length; i++) {
            if(primes[i]) {
                numPrimes++;
                sumPrimes += i;
            }
        }
    }

    public void recordIterationsPerThread(String threadName, int iterations) {
        threadIterations.put(threadName, threadIterations.getOrDefault(threadName, 0) + iterations);
    }

    public void recordDurationPerThread(String threadName, long duration) {
        threadDurations.put(threadName, threadDurations.getOrDefault(threadName, 0L) + duration);
    }
}