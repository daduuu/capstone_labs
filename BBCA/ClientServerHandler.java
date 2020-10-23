package BBCA;

import java.util.Scanner;

public class ClientServerHandler implements Runnable{
    private boolean hasName = false;
    private boolean first = true;
    private String list;

    public ClientServerHandler(){
    }

    public boolean isHasName() {
        return hasName;
    }

    public void setHasName(boolean hasName) {
        this.hasName = hasName;
    }

    public String getList(){
        return list;
    }

    @Override
    public void run() {
        try {
            Message incoming;

            while( (incoming = (Message) BBCA.ChatClient.getSocketIn().readObject()) != null) {
                //handle different headers
                //WELCOME
                //CHAT
                //EXIT
                //System.out.println(incoming);

                if (incoming.getMsgHeader() == Message.MSG_LIST){ // update list of users
                    list = incoming.getMsg();
                    if (first){
                        System.out.println(list); // if first joining, display list of users
                        first = false;
                    }
                    if (!hasName){
                        System.out.print("Enter your name: ");
                    }
                }

                else if (incoming.getMsgHeader() == Message.MSG_SUBMITNAME){ // invalid username. display enter your name again
                    System.out.print("Enter your name: "); 
                }
                else if (incoming.getMsgHeader() == Message.MSG_WELCOME){ // username confirmed
                    hasName = true;
                    System.out.println(incoming.getMsg() + " has joined.");
                }
                else if (incoming.getMsgHeader() == Message.MSG_CHAT){ // chat received
                    String temp = incoming.getMsg();
                    int index = temp.indexOf(" ");
                    System.out.println(temp.substring(0,index) + ": " + temp.substring(index + 1));
                }
                else if (incoming.getMsgHeader() == Message.MSG_PCHAT){ // private chat received
                    String temp = incoming.getMsg();
                    int index = temp.indexOf(" ");
                    System.out.println(temp.substring(0, index) + "(private): " + temp.substring(index+1));
                }
                else if (incoming.getMsgHeader() == Message.MSG_IS_MUTED){ // user is muted
                    System.out.println("You are muted. Type /unmute to unmute yourself.");
                }
                else if (incoming.getMsgHeader() == Message.MSG_MUTE){ // user just became muted
                    String temp = incoming.getMsg();
                    System.out.println("You have been muted by " + temp);
                }
                else if (incoming.getMsgHeader() == Message.MSG_UNMUTE){ // user unmuted himself or herself
                    System.out.println("You are unmuted. You can talk now!");
                }
                else if (incoming.getMsgHeader() == Message.MSG_QUIT){ // someone has left
                    System.out.println(incoming.getMsg() + " has left.");
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