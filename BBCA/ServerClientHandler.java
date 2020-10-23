package BBCA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

import static BBCA.ChatServer.clientList;


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
    public void broadcast(Message msg, String name, boolean only) {
        try {
            System.out.println("Broadcasting -- " + msg.getMsgHeaderName(msg.getMsgHeader()) + " " +msg.getMsg());
            synchronized (clientList) {
                for (ClientConnectionData c : clientList){
                    if (c.getUserName().equals(name) == only) {
                        c.getOut().writeObject(msg);
                        if (only){
                            break;
                        }
                    }
                    // c.getOut().flush();
                }
            }
        } catch (Exception ex) {
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }

    }

    public void list(){
        try{
            String message = "LIST: ";
            synchronized (clientList){
                for (ClientConnectionData c: clientList){
                    if (c.getUserName() != null){
                        message += c.getUserName() + ", ";
                    }
                }
                message = message.substring(0, message.length()-2);
                System.out.println("Broadcasting -- " + message);
                for (ClientConnectionData c: clientList){
                    if (c.getUserName() != null){
                        c.getOut().writeObject(new Message(message, Message.MSG_LIST));
                    }
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
            ObjectInputStream in = client.getInput();

            Message incoming;

            while( (incoming = (Message) in.readObject()) != null) {
                //System.out.println(incoming);

                if (incoming.getMsgHeader() == Message.MSG_LIST){
                    list();
                }

                // the client unmute himself or herself.
                else if (incoming.getMsgHeader() == Message.MSG_UNMUTE){
                    client.setMute(false);
                    broadcast(new Message("", Message.MSG_UNMUTE), client.getUserName(), true);
                }

                // if the client is muted, he or she can't talk and would receive message that he or she is muted
                else if (client.isMute()){
                    broadcast(new Message("", Message.MSG_IS_MUTED), client.getUserName(), true);
                }

                // default CHAT
                else if (incoming.getMsgHeader() == Message.MSG_CHAT) {
                    String chat = incoming.getMsg().trim();
                    if (chat.length() > 0) {
                        broadcast(new Message(String.format("%s %s", client.getUserName(), chat), Message.MSG_CHAT), client.getUserName(), false);
                    }
                }

                // if it receives PCHAT
                else if (incoming.getMsgHeader() == Message.MSG_PCHAT){
                    String message = incoming.getMsg().trim();
                    int index = message.indexOf(" ");
                    String privUser = message.substring(0,index);
                    String chat = message.substring(index+1).trim();
                    if (chat.length() > 0){
                        broadcast(new Message(String.format("%s %s", client.getUserName(), chat), Message.MSG_PCHAT), privUser, true);
                    }
                }

                // if it receives MUTE
                else if (incoming.getMsgHeader() == Message.MSG_MUTE){
                    String message = incoming.getMsg().trim();
                    int index = message.indexOf(" ");
                    String username = message.substring(index+1);
                    Message msg = new Message(client.getUserName(), Message.MSG_MUTE);
                    synchronized (clientList) {
                        for (ClientConnectionData c : clientList){
                            if (c.getUserName().equals(username)){
                                c.setMute(true);
                                c.getOut().writeObject(msg);
                            }
                        }
                    }
                }

                else if(incoming.getMsgHeader() == Message.MSG_NAME){
                    String temp = incoming.getMsg();
                    String userName = temp.trim();
                    boolean unique = true;
                    unique = isUnique(userName, unique);


                    while (!unique || userName.length() == 0 || userName.split(" ").length != 1 || !(userName.matches("[A-Za-z0-9]+"))){
                        synchronized (clientList){
                            client.getOut().writeObject(new Message("", Message.MSG_SUBMITNAME));
                            temp = ((Message) in.readObject()).getMsg();
                            userName = temp.trim();
                            unique = isUnique(userName, unique);

                        }
                    }

                    client.setUserName(userName);
                    //notify all that client has joined
                    broadcast(new Message(client.getUserName(), Message.MSG_WELCOME), "", false);
                    list();
                }

                else if (incoming.getMsgHeader() == Message.MSG_QUIT){
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
            broadcast(new Message(client.getUserName(), Message.MSG_QUIT), "", false);
            list();
            try {
                client.getSocket().close();
            } catch (IOException ex) {}

        }
    }



}