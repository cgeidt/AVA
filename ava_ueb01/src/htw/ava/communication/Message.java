package htw.ava.communication;

import htw.ava.Node;

import java.io.Serializable;

public class Message implements Serializable {

    //Constants which represents the type of the message
    public static final int TYPE_RUMOR = 0;
    public static final int TYPE_NODE_INFO = 1;
    public static final int TYPE_SHUTDOWN_NODES = 2;
    private static final String UNKNOWN_TYPE = "Received Message with unknown type";

    /**
     * Array to match the node type to a string which describes it
     */
    public static final String[] TYPE_MAPPING = {"rumor","info", "shutdown all","rumor 1", "rumor 2", "rumor 3"};

    private int type;
    private Serializable data;
    private String senderId;

    public Message(String senderId, int type, Serializable data){
        this.senderId = senderId;
        this.type = type;
        this.data = data;
    }

    public void process(Node nodeToAffect){
        //Matching the message type to know which operation the node should do
        switch (this.type) {
            case TYPE_NODE_INFO:
                nodeToAffect.processNodeInfoReceived((NodeInfo)data);
                break;
            case TYPE_RUMOR:
                nodeToAffect.processRumorReceived(getSenderId());
                break;
            case TYPE_SHUTDOWN_NODES:
                nodeToAffect.processNodesShutdown(getSenderId());
            default:
                System.out.println(UNKNOWN_TYPE);
                break;
        }
    }


    public int getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public String getSenderId() {
        return senderId;
    }

    public String serialize(){
        return "";
    }


}
