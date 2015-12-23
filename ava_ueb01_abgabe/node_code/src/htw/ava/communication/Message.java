package htw.ava.communication;

import htw.ava.Node;
import htw.ava.NodeManager;

import java.io.Serializable;

/**
 * Class that manages the different messages
 * (kind of container for the different message types)
 */
public class Message implements Serializable {

    //Constants which represents the type of the message
    public static final int TYPE_APPLICATION_RUMOR = 0;
    public static final int TYPE_APPLICATION_NODE_INFO = 1;
    public static final int TYPE_COMMAND_SHUTDOWN_NODES = 2;
    private static final String UNKNOWN_TYPE = "Received Message with unknown type";

    /**
     * Array to match the node type to a string which describes it
     */
    public static final String[] TYPE_MAPPING = {"rumor","info", "shutdown all","rumor 1", "rumor 2", "rumor 3"};

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
     * Apply the chnages to the node caused by the message
     *
     * @param nodeToAffect Node which receives the message
     */
    public void process(Node nodeToAffect){
        //Matching the message type to know which operation the node should do
        switch (this.type) {
            case TYPE_APPLICATION_NODE_INFO:
                nodeToAffect.processNodeInfoReceived((NodeInfo)data);
                break;
            case TYPE_APPLICATION_RUMOR:
                nodeToAffect.processRumorReceived(getSenderId());
                break;
            case TYPE_COMMAND_SHUTDOWN_NODES:
                nodeToAffect.processNodesShutdown(getSenderId());
                break;
            default:
                NodeManager.logger.err(UNKNOWN_TYPE+": "+this.getType());
                break;
        }
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
