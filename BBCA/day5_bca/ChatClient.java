package BBCA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static Socket socket;
    private static BufferedReader socketIn;
    private static PrintWriter out;

    public static BufferedReader getSocketIn() {
        return socketIn;
    }

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

        System.out.print("Enter your name: ");
        String name = userInput.nextLine().trim();
        String temp = String.format("NAME %s", name);
        out.println(temp);

        String line = userInput.nextLine().trim();
        while(!line.toLowerCase().startsWith("/quit")) {
            // default CHAT
            String msg = String.format("CHAT %s", line);

            // check if the client has name
            if (!listener.hasName){
                msg = String.format("NAME %s", line);
            }

            // Check if it is a private PCHAT
            else if (line.startsWith("@")){
                int index = line.indexOf(" ");
                String username = line.substring(1,index);
                msg = String.format("PCHAT %s %s", username, line.substring(index+1));
            }

            // check if the user has typed to mute someone
            else if (line.startsWith("/mute")){
                int index = line.indexOf(" ");
                String username = line.substring(index + 1);
                msg = String.format("MUTE %s", username);
            }

            // check if user wants to unmute himself or herself
            else if (line.equals("/unmute")){
                msg = "UNMUTE";
            }

            // send the message out to server
            out.println(msg);
            line = userInput.nextLine().trim();
        }
        out.println("QUIT");
        out.close();
        userInput.close();
        socketIn.close();
        socket.close();

    }


}