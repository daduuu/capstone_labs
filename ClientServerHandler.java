<<<<<<< HEAD:BBCA/ClientServerHandler.java
package BBCA3;
=======
package bbca;

import java.util.Scanner;
>>>>>>> 0c6d158225094afb764ad932f472f6648b551708:ClientServerHandler.java

public class ClientServerHandler implements Runnable{
    private boolean hasName = false;
    private boolean first = true;
    private String list;

    public ClientServerHandler(){
    }

    public ClientServerHandler(){
    }

    public boolean isHasName() {
        return hasName;
    }

    public void setHasName(boolean hasName) {
        this.hasName = hasName;
<<<<<<< HEAD:BBCA/ClientServerHandler.java
    }

    public String getList(){
        return list;
=======
>>>>>>> 0c6d158225094afb764ad932f472f6648b551708:ClientServerHandler.java
    }

    @Override
    public void run() {
        try {
            String incoming = "";

<<<<<<< HEAD:BBCA/ClientServerHandler.java
            while( (incoming = ChatClient.getSocketIn().readLine()) != null) {
=======
            while( (incoming = bbca.ChatClient.getSocketIn().readLine()) != null) {
>>>>>>> 0c6d158225094afb764ad932f472f6648b551708:ClientServerHandler.java
                //handle different headers
                //WELCOME
                //CHAT
                //EXIT
                //System.out.println(incoming);
<<<<<<< HEAD:BBCA/ClientServerHandler.java

                if (incoming.startsWith("LIST")){
                    list = incoming;
                    if (first){
                        System.out.println(list);
                        first = false;
                    }
                    if (!hasName){
                        System.out.print("Enter your name: ");
                    }
                }

                else if (incoming.equals("SUBMITNAME")){
                    System.out.print("Enter your name: ");
                }
=======


                if (incoming.equals("SUBMITNAME")){
                    System.out.print("Enter your name: ");
                }
>>>>>>> 0c6d158225094afb764ad932f472f6648b551708:ClientServerHandler.java
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
