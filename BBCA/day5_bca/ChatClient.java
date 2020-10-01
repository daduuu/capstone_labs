package BBCA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static Socket socket;
    public static BufferedReader socketIn;
    private static PrintWriter out;

    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);

        System.out.println("What's the server IP? ");
        String serverip = userInput.nextLine();
        System.out.println("What's the server port? ");
        int port = userInput.nextInt();
        userInput.nextLine();

        socket = new Socket(serverip, port);
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // start a thread to listen for server messages
        ClientServerHandler listener = new ClientServerHandler();
        Thread t = new Thread(listener);
        t.start();

        /*while (!listener.hasName){
            System.out.print("Enter your name: ");
            String name = userInput.nextLine().trim();
            out.println("NAME " + name); //out.flush();
        }*/
        System.out.print("Enter your name: ");
        String name = userInput.nextLine().trim();
        out.println("NAME " + name); //out.flush();


        String line = userInput.nextLine().trim();
        while(!line.toLowerCase().startsWith("/quit")) {
            // default CHAT
            String msg = String.format("CHAT %s", line);

            // If it is a private PCHAT
            if (line.startsWith("@")){
                int index = line.indexOf(" ");
                String username = line.substring(1,index);
                msg = String.format("PCHAT %s %s", username, line.substring(index+1));
            }

            out.println(msg);
            line = userInput.nextLine().trim();
        }
        out.println("QUIT");
        out.close();
        userInput.close();
        socketIn.close();
        socket.close();
        
    }

    public static void name(){
        Scanner userInput = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = userInput.nextLine().trim();
        out.println("NAME " + name); //out.flush();
    }
}
