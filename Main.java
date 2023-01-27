import java.io.FileWriter;
import java.util.Arrays;
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
        metrics.writeToFile();
    }
}

class Eratosthenes implements Runnable {
    private int n;
    private boolean primes[];

    public Eratosthenes(int n, boolean primes[], Metrics metrics) {
        this.n = n;
        this.primes = primes;
    }

    @Override
    public void run() {
        // Update all multiples of p
        for (int i = n * n; i <= Main.MAX; i += n) {
            primes[i] = false;
        }
    }
}

class Metrics {
    private long startTime;
    private long endTime;
    private long numPrimes;
    private long sumPrimes;
    private int topTen[];

    public Metrics() {
        numPrimes = 0;
        sumPrimes = 0;
        topTen = new int[10];
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void end() {
        endTime = System.currentTimeMillis();
    }

    public void calculateNumAndSumPrimes(boolean primes[]) {
        for (int i = primes.length - 1; i >= 2; i--) {
            if (primes[i]) {
                numPrimes++;
                sumPrimes += i;
                if (numPrimes <= 10) {
                    topTen[(int) numPrimes - 1] = i;
                }
            }
        }
        // Sort top ten array
        Arrays.sort(topTen);
    }

    public boolean writeToFile() {
        // format
        // <execution time> <total number of primes found> <sum of all primes found>
        // <top ten maximum primes, listed in order from lowest to highest>
        try (FileWriter writer = new FileWriter("primes.txt")) {
            writer.write((endTime - startTime) + " " + numPrimes + " " + sumPrimes + "\n");
            for (int i = 0; i < topTen.length; i++) {
                if (i == topTen.length - 1)
                    writer.write(topTen[i] + "\n");
                else
                    writer.write(topTen[i] + ", ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}