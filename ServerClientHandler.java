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

    private boolean isUnique(String userName, boolean unique) {
        synchronized (clientList) {
            if(clientList.size() != 0){
                for (ClientConnectionData c : clientList) {
                    if (c.getUserName() == null) {
                        continue;
                    } else if (c.getUserName().equals(userName.substring(1))) {
                        unique = false;
                    }
                }
            }
        }
        return unique;
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

            String incoming = "";

            while( (incoming = in.readLine()) != null) {
                System.out.println(incoming);

                // the client unmute himself or herself.
                if (incoming.equals("UNMUTE")){
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
                    synchronized (clientList) {
                        for (ClientConnectionData c : clientList){
                            if (c.getUserName().equals(username)){
                                c.setMute(true);
                                c.getOut().println(msg);
                            }
                        }
                    }
                }

                else if(incoming.startsWith("NAME")){
                    String temp = new String(incoming);
                    String userName = temp.substring(5).trim();
                    boolean unique = true;
                    unique = isUnique(userName, unique);


                    while (!unique || userName.length() == 0 || userName.split(" ").length != 1 || !(userName.matches("[A-Za-z0-9]+"))){
                        synchronized (clientList){
                            client.getOut().println("SUBMITNAME");
                            temp = in.readLine();
                            userName = temp.substring(5).trim();
                            unique = isUnique(userName, unique);

                        }
                    }

                    client.setUserName(userName);
                    //notify all that client has joined
                    broadcast(String.format("WELCOME %s", client.getUserName()), "", false);
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
