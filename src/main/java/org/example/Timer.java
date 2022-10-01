package org.example;

public class Timer {
    private volatile boolean isStopped;

    //Task 1
    public void runTimer() {
        final Object monitor = new Object();
        Thread printThread = new Thread(() -> {
            int counter = -1;
            synchronized (monitor) {
                try {
                    while (!isStopped) {
                        if (++counter == 5) {
                            System.out.println("Прошло 5 секунд");
                            counter = 0;
                        }
                        monitor.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        printThread.start();

        Thread timer = new Thread(() -> {
            final long start = System.currentTimeMillis();
            while (!isStopped) {
                try {
                    long time = System.currentTimeMillis() - start;
                    System.out.printf("С запуска прошло: %02d:%02d:%02d%n", time / 1000 / 3600, time / 1000 / 60 % 60, time / 1000 % 60);
                    Thread.sleep(1000);
                    synchronized (monitor) {
                        monitor.notifyAll();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

            }
        });
        timer.start();
    }
    public void stop(){
        isStopped=true;
    }


}