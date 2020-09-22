package day1_threads;

// There are so many ways to create Threads in Java.
// Check out this link for some we didn't cover.
// http://tutorials.jenkov.com/java-concurrency/creating-and-starting-threads.html

public class Day1_Threads {
    public static void main(String[] args) {
        // Construct runnables
        Counter a = new Counter("A");
        Runnable b = new Counter("B");
        
        // Create a runnable from a Lambda function. Easy!
        Runnable c = () -> {
            for (int i=0; i<10; i++){
                System.out.println("C: " + i);
            }
        };

        // a.run();
        
        // Construct threads, and start them
        Thread t1 = new Thread(a);
        Thread t2 = new Thread(b);
        Thread t3 = new Thread(c);

        //Create a thread with a Lambda function for the passed-in Runnable
        Thread t4 = new Thread(() -> {
            for (int i=0; i<10; i++){
                System.out.println("D: " + i);
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        System.out.println("Main done!");
    }
}

//Make a non public class for Counter, which implements Runnable
class Counter implements Runnable {
    private String name;

    Counter(String name) {
        this.name = name;
    }
    
    @Override
    public void run() {
        for (int i=0; i<10; i++){
            System.out.println(name + ": " + i);
        }
    }
}