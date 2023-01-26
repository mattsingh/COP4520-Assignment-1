import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static int MIN = 2;
    public static int MAX = (int) Math.pow(10, 8);
    public static int NUM_THREADS = 8;
    // private boolean primes[] = new boolean[MAX];
    
    public static void main(String[] args) throws InterruptedException {
        List<int[]> primes = new SynchronizedArrayList<int[]>();
        primes.add(new int[] { 3, 3 });

        ExecutorService threadExecutor = Executors.newFixedThreadPool(8);

        for (int i = 2; i < MAX; i++) {
            threadExecutor.execute(new PrimeFinderThread(i, primes));
        }

        threadExecutor.shutdown();
        threadExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
}

class PrimeFinderThread implements Runnable {
    private int n;
    // private boolean primes[];
    private List<int[]> primes;
    private int primeCounter;

    public PrimeFinderThread(int n, List<int[]> primes) {
        this.n = n;
        this.primes = primes;
        this.primeCounter = 0;
    }

    @Override
    public void run() {
        boolean isPrime = true;
        for (int i = 0; i < primes.size(); i++) {
            int prime[] = primes.get(i);
            while(prime[1] < n)
                prime[1] += prime[0];
            if (prime[1] == n) {
                isPrime = false;
                break;
            }
        }
        if (isPrime) {
            primes.add(new int[] { n, n });
            primeCounter++;
            System.out.println(n + " is prime");
        }
    }
}

class SynchronizedArrayList<T> extends ArrayList<T> {
    private static final long serialVersionUID = 1L;

    @Override
    public synchronized boolean add(T e) {
        return super.add(e);
    }

    @Override
    public synchronized T get(int index) {
        return super.get(index);
    }

    @Override
    public synchronized int size() {
        return super.size();
    }
}