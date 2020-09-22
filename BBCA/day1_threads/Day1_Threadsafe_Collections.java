package day1_threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day1_Threadsafe_Collections {
    
    static List<Integer> regularList = new ArrayList<Integer>();

    // https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedList-java.util.List-
    public static void main(String[] args) {
        //Start this list off with a starting value
        regularList.add(1);
        
        // Collections.synchronizedList creates a wrapper that synchronizes (atomizes) each 
        // individual methods like size(), add(), remove() etc. 
        // This helps prevent really bad stuff from happening like
        // interleaving two adds at the same time
        // But, it doesn't solve the problem totally...
        List<Integer> synchronizedList = Collections.synchronizedList(regularList);


        Thread a = new Thread(new ListFiller(synchronizedList));
        Thread b = new Thread(new ListFiller(synchronizedList));
        Thread c = new Thread(new ListFiller(synchronizedList));
        a.start();
        b.start();
        c.start();
        //wait for both threads to finish
        try {
            a.join();
            b.join();
            c.join();
            System.out.println("Size: " + regularList.size());
            for (int i : regularList)
                System.out.print(i + ",");
        } catch(InterruptedException ex) {}

    }
}

class ListFiller implements Runnable {
    private List<Integer> list;

    public ListFiller(List<Integer> list){
        this.list = list;
    }

    @Override
    public void run() {
        //yes, I know, this loop is kinda dumb. You'll see why.
        // Generally, some kind of loop with end condition checks is how to end 
        // threads. The Thread.stop() method is deprecated and dangerous.
        
        while(true){
            synchronized (list) {
                if (list.size() < 1000)
                    list.add(list.get(list.size() - 1) + 1);
                else
                    break;
            }
        }
    }
} 