import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Eratosthenes {
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
            threadExecutor.execute(new PrimeFinderThread(i, primes, metrics));
        }

        threadExecutor.shutdown();
        threadExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        metrics.end();
        metrics.calculateNumAndSumPrimes(primes);
        metrics.output();

        // Double check
        // for (int i = 2; i < primes.length; i++) {
        //     if(primes[i]) 
        //         if (!isPrime(i))
        //             System.out.println("Error: " + i);
        // }
    }

    private static boolean isPrime(int number) {
        // Corner cases
        if (number <= 1)
            return false;
        if (number <= 3)
            return true;

        // This is checked so that we can skip
        // middle five numbers in below loop
        if (number % 2 == 0 || number % 3 == 0)
            return false;

        for (int i = 5; i * i <= number; i = i + 6)
            if (number % i == 0 || number % (i + 2) == 0)
                return false;

        return true;
    }
}

class PrimeFinderThread implements Runnable {
    private int n;
    private boolean primes[];
    private Metrics metrics;

    public PrimeFinderThread(int n, boolean primes[], Metrics metrics) {
        this.n = n;
        this.primes = primes;
        this.metrics = metrics;
    }

    @Override
    public void run() {
        int iterations = 0;
        // If prime[p] is not changed, then it is a prime
        if (primes[n] == true) {
            // Update all multiples of p
            for (int i = n * n; i <= Eratosthenes.MAX; i += n) {
                primes[i] = false;
                iterations++;
            }
        }
        metrics.threadIterations.put(Thread.currentThread().getName(), iterations);
    }
}

class Metrics {
    public long startTime;
    public long endTime;
    public long numPrimes;
    public long sumPrimes;

    // list of threads and their iterations
    HashMap<String, Integer> threadIterations = new HashMap<String, Integer>();

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

        for (String threadName : threadIterations.keySet()) {
            System.out.println(threadName + ": " + threadIterations.get(threadName));
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
}