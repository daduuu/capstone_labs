package chat_message;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerClientHandler implements Runnable{
    private static final ArrayList<ClientConnectionData> clientList = new ArrayList<>();

    ClientConnectionData client;

    public ServerClientHandler(ClientConnectionData client) {
        this.client = client;
    }

    public void broadcast(String msg){

        try{
            System.out.println("Broadcasting -- " + msg);
            synchronized (clientList){
                for (ClientConnectionData c : clientList){
                    c.getOut().println();

                }
            }
        }
        catch (Exception e){
            System.out.println("Broadcast caught exception: " + e);
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        //TODO Message stub
        try{

            BufferedReader in = client.getInput();
            String username = in.readLine().trim();
            client.setUsername(username);

            //notify all that client has joined
            broadcast(String.format("WELCOME %s", client.getUsername()));



            String incoming = "";

            while((incoming = in.readLine()) != null){
                //handle messages

                if(incoming.startsWith("CHAT")){
                    String chat = incoming.substring(4).trim();
                    if(chat.length() > 0){
                        String msg = String.format("CHAT %s %s", client.getName(), chat);
                        //broadcast message
                        broadcast(msg);
                    }
                }
                else if(incoming.startsWith("QUIT")){
                    break;
                }
            }
        }
        catch (Exception e){
            if(e instanceof SocketException){
                System.out.println("caught socket exception for " + client.getSocket());
            }
            else{
                System.out.println(e);
                e.printStackTrace();
            }
        }

        finally {
            //remove client from clientlist and notifies all
            synchronized (clientList){
                clientList.remove(client);
            }
            System.out.println(client.getName() + " has left.");
            broadcast(String.format("EXIT %s", client.getName()));
            try{
                client.getSocket().close();
            }
            catch (IOException e){}
        }


    }
}
