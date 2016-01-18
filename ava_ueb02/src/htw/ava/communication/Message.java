package htw.ava.communication;

import htw.ava.Node;
import htw.ava.NodeManager;
import htw.ava.communication.massages.Game;

import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Class that manages the different messages
 * (kind of container for the different message types)
 */
public class Message implements Serializable {

    //Constants which represents the type of the message
    public static final int TYPE_APPLICATION_NODE_INFO = 0;
    public static final int TYPE_COMMAND_SHUTDOWN_NODES = 1;
    public static final int TYPE_APPLICATION_GAME = 2;
    public static final int TYPE_APPLICATION_STOP_PLAYING = 3;
    public static final int TYPE_APPLICATION_START_PLAYING = 6;
    public static final int TYPE_APPLICATION_TELL_MONEY = 4;
    public static final int TYPE_APPLICATION_TELL_MONEY_ANSWER = 5;
    public static final String UNKNOWN_TYPE = "Received Message with unknown type";

    /**
     * Array to match the node type to a string which describes it
     */
    public static final String[] TYPE_MAPPING = {"nodeinfo","shutdown", "game"};

    private int type;
    private Serializable data;
    private String senderId;

    /**
     * Creates an Message object
     *
     * @param senderId id of the sender
     * @param type type of the message
     * @param data the message itself
     */
    public Message(String senderId, int type, Serializable data){
        this.senderId = senderId;
        this.type = type;
        this.data = data;
    }


    /**
     *
     * @return type of the message
     */
    public int getType() {
        return type;
    }

    /**
     *
     * @return the message itself
     */
    public Object getData() {
        return data;
    }

    /**
     *
     * @return the id of the sender node
     */
    public String getSenderId() {
        return senderId;
    }

}
