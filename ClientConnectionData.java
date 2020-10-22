package bbca;

import java.io.*;
import java.net.Socket;

public class ClientConnectionData extends Message implements Serializable {
    public static final long serialVersionUID = 1L;

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream out;
    private String name;
    private String userName;
    private boolean mute;

    public ClientConnectionData(Socket socket, ObjectInputStream input, ObjectOutputStream out, String name) {
        this.socket = socket;
        this.input = input;
        this.out = out;
        this.name = name;
        this.mute = false;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    
}
