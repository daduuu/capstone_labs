package bbca;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream socketIn;




    public static Socket getSocket() {
        return socket;
    }

    public static ObjectOutputStream  getOut() {
        return out;
    }

    public static ObjectInputStream  getSocketIn() {
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
        out = new ObjectOutputStream(socket.getOutputStream());
        socketIn = new ObjectInputStream(socket.getInputStream());


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
        String line = "";
        Message m;

        while(!line.toLowerCase().startsWith("/quit")) {
            while(!listener.isHasName()) {
                m = new Message(String.format("NAME %s", name), Message.MSG_NAME);
                out.writeObject(m);
                name = userInput.nextLine().trim();
                if(listener.isHasName()){
                    line = name;
                }
            }
            // default CHAT
            m = new Message(String.format("CHAT %s", line), Message.MSG_CHAT);

            // If it is a private PCHAT
            if (line.startsWith("@")){
                int index = line.indexOf(" ");
                String username = line.substring(1,index);
                m = new Message(String.format("PCHAT %s %s", username, line.substring(index+1)), Message.MSG_PCHAT);
            }

            else if (line.startsWith("/mute")){
                int index = line.indexOf(" ");
                String username = line.substring(index + 1);
                m = new Message(String.format("MUTE %s", username), Message.MSG_MUTE);
            }

            else if (line.equals("/unmute")){
                m = new Message("UNMUTE", Message.MSG_UNMUTE);
            }

            out.writeObject(m);
            line = userInput.nextLine().trim();
        }
        out.writeObject(new Message("QUIT", Message.MSG_QUIT));
        out.close();
        userInput.close();
        socketIn.close();
        socket.close();
        
    }


}
