package bbca;

import java.io.Serializable;

public class Message implements Serializable {
    public static final long serialVersionUID = 1L;

    public static final int MSG_CHAT = 0;
    public static final int MSG_PCHAT = 1;
    public static final int MSG_UNMUTE = 2;
    public static final int MSG_NAME = 3;
    public static final int MSG_QUIT = 4;
}
