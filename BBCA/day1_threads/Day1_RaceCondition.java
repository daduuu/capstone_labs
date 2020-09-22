package day1_threads;

import java.util.concurrent.locks.ReentrantLock;

public class Day1_RaceCondition {

    static MyInt count = new MyInt();
    public static void main(String[] args) {
        for (int i = 1; i <= 100; i++) {
            final int id = i;
            (new Thread(() -> {
                for (int j = 0; j < 10; j++){
                    count.increment(id);
                }
            })).start(); 
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex){}

        System.out.println("After 1 second, count is " + count.getCount());
    }
}

class MyInt {
    private int count = 0;
    // ReentrantLock lock = new ReentrantLock();

    // The synchronized keyword for a method is the same as wrapper the interior of the method with the 
    // commented-out synchronized block
    public synchronized void increment(int id) {
        // synchronized (this) {
        // lock.lock();
        int oldcount = count;
        int newcount = oldcount + 1;
        System.out.println(id + ":" + oldcount + "->" + newcount);
        count = newcount;
        // lock.unlock();
        // }
    }

    public int getCount(){
        return count;
    }
}
