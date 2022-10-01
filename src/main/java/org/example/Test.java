package org.example;

public class Test {
    public static void main(String[] args) {

        new FizzBuzz(15).doFizzBuzz();


        var timer = new Timer();

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                System.out.println("Starting timer");
                timer.runTimer();
                Thread.sleep(6100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            timer.stop();
            System.out.println("Stopping timer");
        }).start();


    }
}
