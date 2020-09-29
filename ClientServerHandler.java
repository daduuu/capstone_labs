package chat_message;

import java.io.BufferedReader;

public class ClientServerHandler implements Runnable{
    private BufferedReader socketIn;

    public ClientServerHandler(BufferedReader socketIn) {
        this.socketIn = socketIn;
    }

    @Override
    public void run() {
        try{
            String incoming = "";

            while((incoming = socketIn.readLine()) != null){
                //handle different headers
                //WELCOME
                //CHAT
                //EXIT
                System.out.print(incoming);
            }


        }
        catch (Exception e){
            System.out.println("Exception caught in listener - " + e.getMessage());
        }
        finally {
            System.out.println("Chat Listener Exiting... ");


        }

    }
}
