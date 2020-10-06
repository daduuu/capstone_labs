package BBCA;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

import static BBCA.ChatServer.clientList;


public class ServerClientHandler implements Runnable{

    // Maintain data about the client serviced by this thread
    ClientConnectionData client;

    public ServerClientHandler(ClientConnectionData client) {
        this.client = client;
    }

    // check if no one else has the username
    private boolean isUnique(String userName) {
        synchronized (clientList) {
            if(clientList.size() != 0){
                for (ClientConnectionData c : clientList) {
                    if (c.getUserName() == null) {
                        continue;
                    } else if (c.getUserName().equals(userName)) {
                            return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Broadcasts a message to specific clients connected to the server.
     *
     * If  boolean only is true, it sends message to only the client with String name
     *
     * If not, send message to all the clients except the client with String name
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

            String incoming = "";
            while( (incoming = in.readLine()) != null) {
                System.out.println(incoming);

                // check if client sent name
                if (incoming.startsWith("NAME")){
                    String userName = incoming.substring(5).trim();

                    // check if it is a valid name
                    if (userName.length() != 0 && !userName.contains(" ") && isUnique(userName) && userName.matches("[A-Za-z0-9]+")){
                        client.setUserName(userName);
                        broadcast(String.format("WELCOME %s", client.getUserName()), "", false);
                    }

                    // request the user to send name again
                    else {
                        System.out.println("Broadcasting -- SUBMITNAME");
                        client.getOut().println("SUBMITNAME");
                    }
                }

                // the client unmute himself or herself.
                else if (incoming.equals("UNMUTE")){
                    client.setMute(false);
                    broadcast("UNMUTE", client.getUserName(), true);
                }

                // if the client is muted, he or she can't talk and would receive message that he or she is muted
                else if (client.isMute()){
                    broadcast("MUTED", client.getUserName(), true);
                }

                // default CHAT
                else if (incoming.startsWith("CHAT")) {
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

                // if it receives MUTE
                else if (incoming.startsWith("MUTE")){
                    String message = incoming.substring(4).trim();
                    int index = message.indexOf(" ");
                    String username = message.substring(index+1);
                    String msg = String.format("MUTE %s", client.getUserName());

                    // mute client with String username
                    synchronized (clientList) {
                        for (ClientConnectionData c : clientList){
                            if (c.getUserName().equals(username)){
                                System.out.println("Broadcasting -- MUTE");
                                c.setMute(true);
                                c.getOut().println(msg);
                            }
                        }
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
            broadcast(String.format("EXIT %s", client.getUserName()), "", false);
            try {
                client.getSocket().close();
            } catch (IOException ex) {}

        }
    }



}