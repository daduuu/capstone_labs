package bbca;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

import static bbca.ChatServer.clientList;


public class ServerClientHandler implements Runnable{

    // Maintain data about the client serviced by this thread
    ClientConnectionData client;

    public ServerClientHandler(ClientConnectionData client) {
        this.client = client;
    }

    /**
     * Broadcasts a message to all clients connected to the server.
     */
    public void broadcast(String msg, String name, boolean only) {
        try {
            System.out.println("Broadcasting -- " + msg);
            synchronized (clientList) {
                for (ClientConnectionData c : clientList){
                    if (c.getUserName().equals(name) == only)
                        c.getOut().println(msg);
                    // c.getOut().flush();
                }
            }
        } catch (Exception ex) {
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            BufferedReader in = client.getInput();
            String temp = in.readLine();
            String userName = temp.substring(5).trim();
            boolean unique = true;

            if(clientList.size() != 0){
                for (ClientConnectionData c : clientList){
                    if(c.getUserName() == null){
                        continue;
                    }
                    else if(c.getUserName().equals(userName.substring(1))){
                        unique = false;
                    }
                }
            }


            while (userName.startsWith("*") || !unique || userName.length() == 0 || userName.split(" ").length != 1 || !(userName.matches("[A-Za-z0-9]+"))){
                synchronized (clientList){
                    client.getOut().println("SUBMITNAME");
                    temp = in.readLine();
                    userName = temp.substring(5).trim();
                    unique = true;
                    if(clientList.size() != 0){
                        for (ClientConnectionData c : clientList){
                            if(c.getUserName() == null){
                                continue;
                            }
                            else if(c.getUserName().equals(userName.substring(1))){
                                unique = false;
                            }
                        }
                    }


                }
            }

            client.setUserName(userName);
            //notify all that client has joined
            broadcast(String.format("WELCOME %s", client.getUserName()), "", false);
            client.getOut().printf("WELCOME %s", userName);



            String incoming = "";

            while( (incoming = in.readLine()) != null) {
                System.out.println(incoming);


                // default CHAT
                if (incoming.startsWith("CHAT")) {
                    String chat = incoming.substring(4).trim();
                    if (chat.length() > 0) {
                        String msg = String.format("CHAT %s %s", client.getUserName(), chat);
                        broadcast(msg, client.getUserName(), false);
                    }
                }

                // if it receives PCHAT
                else if (incoming.startsWith("PCHAT")){
                    String message = incoming.substring(5).trim();
                    int index = message.indexOf(" ");
                    String privUser = message.substring(0,index);
                    String chat = message.substring(index+1);
                    if (chat.length() > 0){
                        String msg = String.format("PCHAT %s %s", client.getUserName(), chat);
                        broadcast(msg, privUser, true);
                    }
                }

                else if (incoming.startsWith("QUIT")){
                    break;
                }
            }
        } catch (Exception ex) {
            if (ex instanceof SocketException) {
                System.out.println("Caught socket ex for " +
                        client.getName());
            } else {
                System.out.println(ex);
                ex.printStackTrace();
            }
        } finally {
            //Remove client from clientList, notify all
            synchronized (clientList) {
                clientList.remove(client);
            }
            System.out.println(client.getName() + " has left.");
            broadcast(String.format("EXIT %s", client.getUserName()), "", true);
            try {
                client.getSocket().close();
            } catch (IOException ex) {}

        }
    }

}
