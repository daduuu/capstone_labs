package day2_basicnetworks;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CapitalizeServer {
    public static final int PORT = 54321;

    public static void main(String[] args) throws Exception {
        //try with resources, closes serverSocket in case of error
        try ( ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("Server started.");
            System.out.println("Local IP: "
                    + Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Local Port: " + listener.getLocalPort());

            // In Java, you should never create threads directly; 
            // instead, employ a thread pool and use an executor service to manage the threads.
            // Limiting the thread pool size protects us against being swamped with millions of clients.
            
            ExecutorService pool = Executors.newFixedThreadPool(50);
            while (true) {
                Socket socket = listener.accept();
                Runnable r = new CapitalizeHandler(socket);
                // Thread t = new Thread(r);
                // t.start();           
                pool.execute(r);     
            }
        }
    }

    static class CapitalizeHandler implements Runnable {
        private Socket socket;

        CapitalizeHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            System.out.println("Connected: " + socket);
            try {
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //autoflush

                //listen to the client for text, and send back capitlized text
                while(in.hasNextLine()){
                    String text = in.nextLine();
                    System.out.printf("%s:%d sent \"%s\"\n", 
                        socket.getInetAddress(), socket.getPort(), text);
                    out.println(text.toUpperCase());
                    // out.flush();
                }

            } catch (Exception e) {
                System.out.println("Error: " + socket);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }

        }
    }
}
