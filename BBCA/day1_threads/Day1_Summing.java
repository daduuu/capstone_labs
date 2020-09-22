package day1_threads;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.lang.Runnable;

public class Day1_Summing {
    public static void main(String[] args) {
        int[] numbers = new int[10000];
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = i+1;

        int split = numbers.length/2;
        
        //Sum up the numbers in two parts.
        PartialSummer firstHalf = new PartialSummer(numbers, 0, split);
        PartialSummer secondHalf = new PartialSummer(numbers, split, numbers.length);

        // Let's run one summer on a new thread.
        Thread t = new Thread(firstHalf);
        t.start(); // this call is "non-blocking" - does not wait to finish

        //Let's run the other summer on this thread (the "main" thread)
        secondHalf.run(); //this call is "blocking" - waits to finish
        // We could easily run this on a third thread, however.
        // Thread t2 = new Thread(secondHalf);
        // t2.start();

        //Then, we wait for both to finish.
        try {
            t.join();
            // t2.join();
        } catch( InterruptedException ex) {}

        System.out.println(firstHalf.getSum() + secondHalf.getSum());


    }
}

class PartialSummer implements Runnable {
    private int[] list;
    private int start;
    private int end;
    private int sum;
    public PartialSummer(int[] list, int start, int end) {
        this.list = list;
        this.start = start;
        this.end = end;
    }
    
    @Override
    public void run() {
        sum = 0;
        for (int i = start; i < end; i++)
            sum += list[i];
    }

    public int getSum() {
        return sum;
    }


}
