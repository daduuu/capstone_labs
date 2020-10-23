package BBCA;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static Socket socket;
    private static ObjectInputStream socketIn;
    private static ObjectOutputStream out;



    public static Socket getSocket() {
        return socket;
    }

    public static ObjectInputStream getSocketIn() {
        return socketIn;
    }

    public static ObjectOutputStream getOut() {
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
        socketIn = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());

        // start a thread to listen for server messages
        ClientServerHandler listener = new ClientServerHandler();
        Thread t = new Thread(listener);
        t.start();

        /*while (!listener.hasName){
            System.out.print("Enter your name: ");
            String name = userInput.nextLine().trim();
            out.println("NAME " + name); //out.flush();
        }*/


        //out.writeObject(new Message("LIST", Message.MSG_LIST));
        System.out.print("Enter your name: ");
        String name = userInput.nextLine().trim();
        String line = "";
        Message m;

        while(!line.toLowerCase().startsWith("/quit")) {
            while(!listener.isHasName()) {
                m = new Message(name, Message.MSG_NAME);
                out.writeObject(m);
                name = userInput.nextLine().trim();
                if(listener.isHasName()){
                    line = name;
                }
            }
            // default CHAT
            m = new Message(line, Message.MSG_CHAT);

            // If it is a private PCHAT
            if (line.startsWith("@")){
                String[] list = line.split(" ");
                int numUsers = 0;
                int index = 0;
                for (int i = 0; i < list.length; i++){
                    if (list[i].startsWith("@")){
                        numUsers++;
                        index += list[i].length();
                    }
                    else {
                        break;
                    }
                }
                if (index < line.length()){
                    String chat = line.substring(index+1);
                    for (int i = 0; i < numUsers; i++){
                        m = new Message(String.format("%s %s", list[i].substring(1), chat), Message.MSG_PCHAT);
                        out.writeObject(m);
                    }
                }
            }

            else if (line.startsWith("/mute")){
                int index = line.indexOf(" ");
                String username = line.substring(index + 1);
                m = new Message(username, Message.MSG_MUTE);
                out.writeObject(m);
            }

            else if (line.equals("/unmute")){
                m = new Message("", Message.MSG_UNMUTE);
                out.writeObject(m);
            }

            else if (line.equals("/whoishere")){
                System.out.println(listener.getList());
            }
            else {
                out.writeObject(m);
            }

            line = userInput.nextLine().trim();
        }
        out.writeObject(new Message("", Message.MSG_QUIT));
        out.close();
        userInput.close();
        socketIn.close();
        socket.close();

    }


}