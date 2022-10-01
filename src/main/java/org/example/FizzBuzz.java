package org.example;

import java.util.concurrent.*;

public class FizzBuzz {
    private final BlockingQueue<Integer> numbersQueue = new LinkedBlockingDeque<>();
    private final BlockingQueue<String> toPrintQueue = new LinkedBlockingDeque<>();
    private final int limit;
    private volatile boolean checkedFizz;
    private volatile boolean checkedBuzz;
    private volatile boolean checkedFizzBuzz;

    public FizzBuzz(int n) {
        this.limit = n;
    }

    Runnable runnableA = () -> {
        synchronized (numbersQueue) {
            try {
                while (!numbersQueue.isEmpty()) {
                    if (fizz(numbersQueue.peek())) {
                        toPrintQueue.put(String.format("fizz (%d)", numbersQueue.poll()));
                    } else {
                        checkedFizz = true;
                        numbersQueue.wait();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    };

    Runnable runnableB = () -> {
        synchronized (numbersQueue) {
            try {
                while (!numbersQueue.isEmpty()) {
                    if (buzz(numbersQueue.peek())) {
                        toPrintQueue.put(String.format("buzz (%d)", numbersQueue.poll()));
                    } else {
                        checkedBuzz = true;
                        numbersQueue.wait();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    };

    Runnable runnableC = () -> {
        synchronized (numbersQueue) {
            try {
                while (!numbersQueue.isEmpty()) {
                    if (fizzbuzz(numbersQueue.peek())) {
                        toPrintQueue.put(String.format("fizzbuzz (%d)", numbersQueue.poll()));
                    } else {
                        checkedFizzBuzz = true;
                        numbersQueue.wait();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    };

    Runnable runnableD = () -> {
        while (!Thread.currentThread().isInterrupted()) {
            number();
        }
    };


    public void doFizzBuzz() {
        for (int i = 1; i <= limit; i++) numbersQueue.add(i);

        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        threadPool.execute(runnableA);
        threadPool.execute(runnableB);
        threadPool.execute(runnableC);
        threadPool.execute(runnableD);
        threadPool.shutdown();
    }

    private static boolean fizz(int num) {
        return num % 3 == 0 && num % 5 != 0;
    }

    private static boolean buzz(int num) {
        return num % 5 == 0 && num % 3 != 0;
    }

    private static boolean fizzbuzz(int num) {
        return num % 3 == 0 && num % 5 == 0;
    }

    private void number() {
        try {
            String num = null;

            if (checkedFizz && checkedBuzz && checkedFizzBuzz) {
                synchronized (numbersQueue) {
                    var temp = numbersQueue.poll(10, TimeUnit.MILLISECONDS);
                    if (temp != null) {
                        num = String.valueOf(temp);
                        checkedFizz = false;
                        checkedBuzz = false;
                        checkedFizzBuzz = false;
                    }
                }

            } else {
                num = toPrintQueue.poll(10, TimeUnit.MILLISECONDS);
            }

            if (numbersQueue.isEmpty() && toPrintQueue.isEmpty()) {
                Thread.currentThread().interrupt();
            }

            if (num != null) {
                System.out.println(num);
                synchronized (numbersQueue) {
                    numbersQueue.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
