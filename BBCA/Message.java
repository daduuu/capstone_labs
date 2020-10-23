package BBCA3;


import java.io.Serializable;

public class Message implements Serializable {
    public static final long serialVersionUID = 1L;

    private String msg;

    public static final int MSG_CHAT = 0;
    public static final int MSG_PCHAT = 1;
    public static final int MSG_UNMUTE = 2;
    public static final int MSG_MUTE = 3;
    public static final int MSG_IS_MUTED= 4;
    public static final int MSG_NAME = 5;
    public static final int MSG_QUIT = 6;
    public static final int MSG_SUBMITNAME =7;
    public static final int MSG_WELCOME = 8;
    public static final int MSG_EXIT = 9;

    private int msgHeader;

    public Message(String msg, int msgHeader) {
        this.msg = msg;
        this.msgHeader = msgHeader;
    }

    public int getMsgHeader() {
        return msgHeader;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "Message type: " + msgHeader;
    }


}
