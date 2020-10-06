package BBCA;

public class ClientServerHandler implements Runnable{
    private boolean hasName = false;

    public boolean isHasName() {
        return hasName;
    }

    public ClientServerHandler(){

    }

    @Override
    public void run() {
        try {
            String incoming = "";

            while( (incoming = BBCA.ChatClient.getSocketIn().readLine()) != null) {

                // check if user needs to set name
                if (incoming.equals("SUBMITNAME")){
                    System.out.print("Enter your name: ");
                }

                // user has successfully set his or her name; welcome him or her
                else if (incoming.startsWith("WELCOME")){
                    hasName = true; // check user's status of having name
                    System.out.println(incoming.substring(8) + " has joined.");
                }

                // someone texted in default chat
                else if (incoming.startsWith("CHAT")){
                    incoming = incoming.substring(5);
                    int index = incoming.indexOf(" ");
                    System.out.println(incoming.substring(0,index) + ": " + incoming.substring(index + 1));
                }

                // someone private chatted to only this user
                else if (incoming.startsWith("PCHAT")){
                    incoming = incoming.substring(6);
                    int index = incoming.indexOf(" ");
                    System.out.println(incoming.substring(0, index) + "(private): " + incoming.substring(index+1));
                }

                // send that user is muted currently
                else if (incoming.equals("MUTED")){
                    System.out.println("You are muted. Type /unmute to unmute yourself.");
                }

                // send that user has just been muted by [username]
                else if (incoming.startsWith("MUTE")){
                    incoming = incoming.substring(5);
                    System.out.println("You have been muted by " + incoming);
                }

                // send that user has successfully unmuted himself or herself
                else if (incoming.startsWith("UNMUTE")){
                    System.out.println("You are unmuted. You can talk now!");
                }

                // user has left
                else if (incoming.startsWith("EXIT")){
                    System.out.println(incoming.substring(5) + " has left.");
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener - " + ex);
        } finally{
            System.out.println("Client Listener exiting");
        }
    }
}