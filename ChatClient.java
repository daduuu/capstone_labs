package chat_message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static Socket socket;
    private static BufferedReader socketIn;
    private static PrintWriter out;


    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("What's your server IP ");
        String serverIP = input.nextLine();
        System.out.println("What is your server Port");
        int port = input.nextInt();
        input.nextLine();

        socket = new Socket(serverIP, port);
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);


        //start a thread to listen to server messages
        ClientServerHandler listener = new ClientServerHandler(socketIn);
        Thread t = new Thread(listener);
        t.start();


        System.out.print("Chat session has started - enter a name: ");
        String name = input.nextLine().trim();
        out.println(name);

        String line = input.nextLine().trim();
        while(!line.toLowerCase().startsWith("/quit")){
            String msg = String.format("CHAT %s", line);
            out.println(msg);
            line = input.nextLine().trim();
        }
        out.println("QUIT");
        out.close();
        input.close();
        socketIn.close();
        socket.close();
    }
}
