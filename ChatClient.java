package bbca;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static Socket socket;
    private static BufferedReader socketIn;
    private static PrintWriter out;



    public static Socket getSocket() {
        return socket;
    }

    public static BufferedReader getSocketIn() {
        return socketIn;
    }

    public static PrintWriter getOut() {
        return out;
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

        /*while (!listener.hasName){
            System.out.print("Enter your name: ");
            String name = userInput.nextLine().trim();
            out.println("NAME " + name); //out.flush();
        }*/


        boolean naming = true;
        System.out.print("Enter your name: ");

       while(!listener.hasName) {
           String name = userInput.nextLine().trim();
           if(!name.startsWith("*")){
               System.out.print("Enter your name: ");
               continue;
           }
           String temp = String.format("NAME %s", name.substring(1));
           out.println(temp);
       }






        String line = userInput.nextLine().trim();
        String msg = "";
        while(!line.toLowerCase().startsWith("/quit")) {

            // default CHAT
            if(line.startsWith("*")){
                 msg = String.format("CHAT %s", line);
            }

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


}
