import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CopyOnWriteArrayList;

public class PrimeNumbersFinder {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = 0;

        while (true) {
            System.out.print("Введіть число N (від 0 до 1000): ");
            N = scanner.nextInt();
            if (N >= 0 && N <= 1000) {
                break;
            } else {
                System.out.println("Помилка: введіть число в діапазоні від 0 до 1000.");
            }
        }
        
        int numThreads = 4;
        
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<List<Integer>>> futures = new ArrayList<>();
        CopyOnWriteArrayList<Integer> primes = new CopyOnWriteArrayList<>();

        int range = N / numThreads;
        
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

        for (int i = 0; i < numThreads; i++) {
            int start = i * range;
            int end = (i == numThreads - 1) ? N : start + range;

            Callable<List<Integer>> task = new PrimeTask(start, end);
            Future<List<Integer>> future = executorService.submit(task);
            futures.add(future);
        }

        for (Future<List<Integer>> future : futures) {
            try {
                if (!future.isCancelled()) {
                    primes.addAll(future.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Помилка при виконанні завдання: " + e.getMessage());
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        executorService.shutdown();

        System.out.println("Прості числа до " + N + ": " + primes);
        System.out.println("Час виконання: " + (endTime - startTime.get()) + " мс");
    }
}

class PrimeTask implements Callable<List<Integer>> {
    private final int start;
    private final int end;

    public PrimeTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public List<Integer> call() {
        List<Integer> primeNumbers = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            if (isPrime(i)) {
                primeNumbers.add(i);
            }
        }
        return primeNumbers;
    }

    private boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}