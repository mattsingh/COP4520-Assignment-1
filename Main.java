import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static int MIN = 2;
    public static int MAX = (int) Math.pow(10, 8);
    public static int NUM_THREADS = 8;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService threadExecutor = Executors.newFixedThreadPool(8);
        List<PrimeFinderThread> threads = new ArrayList<>();

        int range = (MAX - MIN) / NUM_THREADS;
        for (int i = 0; i < NUM_THREADS; i++) {
            int start = MIN + (i * range);
            int end = start + range;
            threads.add(new PrimeFinderThread(start, end));
        }

        for (PrimeFinderThread thread : threads) {
            threadExecutor.execute(thread);
        }
        threadExecutor.shutdown();
        threadExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
}

class PrimeFinderThread implements Runnable {
    private int start;
    private int end;
    private List<Integer> primes;

    public PrimeFinderThread(int start, int end) {
        this.start = start;
        this.end = end;
        this.primes = new ArrayList<>();
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            if (isPrime(i)) {
                primes.add(i);
            }
        }
        System.out.println(start + " - " + end);
        System.out.println(primes.size());
    }

    private boolean isPrime(int number) {
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