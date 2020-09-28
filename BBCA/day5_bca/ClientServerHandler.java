package BBCA;

import static BBCA.ChatClient.*;

public class ClientServerHandler implements Runnable{
    public boolean hasName = false;

    public ClientServerHandler(){

    }

    @Override
    public void run() {
        try {
            String incoming = "";

            while( (incoming = socketIn.readLine()) != null) {
                //handle different headers
                //WELCOME
                //CHAT
                //EXIT
                //System.out.println(incoming);


                if (incoming.equals("SUBMITNAME")){
                    //System.out.println("SOMETHING IS WRONG");
                    name();
                }
                else if (incoming.startsWith("WELCOME")){
                    hasName = true;
                    System.out.println(incoming.substring(8) + " has joined.");
                }
                else if (incoming.startsWith("CHAT")){
                    incoming = incoming.substring(5);
                    String[] list = incoming.split(" ");
                    System.out.println("NEED CHAT FIXED");
                }
                else if (incoming.startsWith("PCHAT")){
                    //System.out.println("");
                }
                else if (incoming.startsWith("EXIT")){
                    System.out.println(incoming.substring(5) + " has left.");
                }
                //System.out.println(incoming);
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener - " + ex);
        } finally{
            System.out.println("Client Listener exiting");
        }
    }
}
