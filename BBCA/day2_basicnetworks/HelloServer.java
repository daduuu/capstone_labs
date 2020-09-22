package day2_basicnetworks;

import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
public class HelloServer {
    public static final int PORT = 54321;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Hello Server has started.");
        System.out.println("Local IP: " 
            + Inet4Address.getLocalHost().getHostAddress());
        System.out.println("Local Port: " + serverSocket.getLocalPort());

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println(socket);
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            //Get the name sent by the client
            String name = in.nextLine();
            out.println("Hello " + name);
            out.flush();

            // socket.close();
        }
    }
}
