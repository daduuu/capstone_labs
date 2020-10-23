package BBCA3;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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


        out.println("LIST");
        System.out.print("Enter your name: ");
        String name = userInput.nextLine().trim();
        String line = "";

        while(!line.toLowerCase().startsWith("/quit")) {
            while(!listener.isHasName()) {
                String temp = String.format("NAME %s", name);
                out.writeObject(temp);
                name = userInput.nextLine().trim();
                if(listener.isHasName()){
                    line = name;
                }
            }
            // default CHAT
            String msg = String.format("CHAT %s", line);

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
                String chat = line.substring(index+1);
                for (int i = 0; i < numUsers; i++){
                    msg = String.format("PCHAT %s %s", list[i].substring(1), chat);
                    out.writeObject(msg);
                }

                /*int index = line.indexOf(" ");
                String username = line.substring(1,index);
                msg = String.format("PCHAT %s %s", username, line.substring(index+1));*/
            }

            else if (line.startsWith("/mute")){
                int index = line.indexOf(" ");
                String username = line.substring(index + 1);
                msg = String.format("MUTE %s", username);
                out.writeObject(msg);
            }

            else if (line.equals("/unmute")){
                msg = "UNMUTE";
                out.writeObject(msg);
            }

            else if (line.equals("/whoishere")){
                System.out.println(listener.getList());
            }
            else {
                out.writeObject(msg);
            }

            line = userInput.nextLine().trim();
        }
        out.writeObject("QUIT");
        out.close();
        userInput.close();
        socketIn.close();
        socket.close();

    }


}