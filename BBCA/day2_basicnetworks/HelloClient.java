package day2_basicnetworks;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class HelloClient {
    public static final String SERVER_IP = "localhost"; // "24.186.145.220";
    public static final int SERVER_PORT = 54321;

    public static void main(String[] args) throws Exception {
        Scanner userIn = new Scanner(System.in);
        System.out.println("What is your name?");
        String userName = userIn.nextLine();
        userIn.close();

        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        Scanner in = new Scanner(socket.getInputStream());
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        out.println(userName);
        out.flush();
        System.out.println("Sent name to server.");
        System.out.println("Server Response: " + in.nextLine());
        socket.close();
    }
}
