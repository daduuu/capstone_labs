package bbca;

import java.util.Scanner;

public class ClientServerHandler implements Runnable{
    public boolean hasName = false;

    public ClientServerHandler(){

    }

    @Override
    public void run() {
        try {
            String incoming = "";

            while( (incoming = bbca.ChatClient.getSocketIn().readLine()) != null) {
                //handle different headers
                //WELCOME
                //CHAT
                //EXIT
                //System.out.println(incoming);


                if (incoming.equals("SUBMITNAME")){
                    System.out.print("Enter your name: ");
                }
                else if (incoming.startsWith("WELCOME")){
                    hasName = true;
                    System.out.println(incoming.substring(8) + " has joined.");
                }
                else if (incoming.startsWith("CHAT")){
                    incoming = incoming.substring(5);
                    int index = incoming.indexOf(" ");
                    System.out.println(incoming.substring(0,index) + ": " + incoming.substring(index + 1));
                }
                else if (incoming.startsWith("PCHAT")){
                    incoming = incoming.substring(6);
                    int index = incoming.indexOf(" ");
                    System.out.println(incoming.substring(0, index) + "(private): " + incoming.substring(index+1));
                }
                else if (incoming.equals("MUTED")){
                    System.out.println("You are muted. Type /unmute to unmute yourself.");
                }
                else if (incoming.startsWith("MUTE")){
                    incoming = incoming.substring(5);
                    System.out.println("You have been muted by " + incoming);
                }
                else if (incoming.startsWith("UNMUTE")){
                    System.out.println("You are unmuted. You can talk now!");
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
