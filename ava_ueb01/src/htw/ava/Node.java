package htw.ava;

import htw.ava.communication.Message;
import htw.ava.communication.NodeClient;
import htw.ava.communication.NodeInfo;
import htw.ava.communication.NodeServer;
import htw.ava.communication.massages.Rumor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by cgeidt on 21.10.2015.
 */
public class Node {
    private Rumor rumor;
    private NodeServer nodeServer;
    private ArrayList<NodeInfo> neighbours;

    public Node(String id, String hostname, int port, ArrayList<NodeInfo> neighbours) {
        this.rumor = new Rumor();
        initNode(id, hostname, port, neighbours);
    }

    public Node(NodeInfo nodeInfo, ArrayList<NodeInfo> neighbours) {
        this.rumor = new Rumor();
        initNode(nodeInfo.getId(), nodeInfo.getHostname(), nodeInfo.getPort(), neighbours);
    }

    private void initNode(String id, String hostname, int port, ArrayList<NodeInfo> neighbours){
        this.neighbours = neighbours;
        startServer(id, hostname, port);
    }


    private void startServer(String id, String hostname, int port){
        this.nodeServer = new NodeServer(id, hostname, port, this);
        //listener thread
        Thread listenerThread = new Thread(nodeServer);
        listenerThread.start();
    }


    /**
     * send a message to all neigbours
     *
     * @param msg object which conains the message
     */
    private void sendMessage(Message msg) {
        this.sendMessage(msg, -1, "-1");
    }

    /**
     * send a message to a spezified amount of neighbours
     *
     * @param msg object which conains the message
     * @param limit the limit how much neighbours should get a message
     */
    private void sendMessage(Message msg, int limit) {
        this.sendMessage(msg, limit, "-1");
    }

    /**
     * send message to all beighbours expect one
     *
     * @param msg object which conains the message
     * @param except the id of a neighbour which shouldnt get the message
     */
    private void sendMessage(Message msg, String except) {
        this.sendMessage(msg, -1, except);
    }

    /**
     *
     * Send a message to other nodes
     *
     * @param msg object which contains the message
     * @param limit the limit how much neighbours should get a message
     * @param except the id of a neighbour which shouldnt get the message
     */
    private void sendMessage(Message msg, int limit, String except) {
        int count = 0;
        for (NodeInfo nodeInfo : neighbours) {
            // checks if current node is the one which shouldnt get the message
            if (nodeInfo.getId().equals(except)) {
                continue;
            }
            try {
                //sending message
                Thread sendThread = new Thread(new NodeClient(nodeInfo.getHostname(), nodeInfo.getPort(), msg));
                sendThread.start();
                System.out.println("Sent message of type " + Message.TYPE_MAPPING[msg.getType()] + " to node " + nodeInfo.getId());
            } catch (IOException ex) {
                System.err.println("------------------------------------------------------------");
                System.err.println(ex.getMessage());
                System.err.println("Could not find node with id " + nodeInfo.getId());
                System.err.println("Hostname/IP:Port = " + nodeInfo.getHostname() + ":" + nodeInfo.getPort());
                System.err.println("------------------------------------------------------------");
            }
            //checks if limit is set
            if (limit != -1) {
                //checks limit constrain
                if (count >= limit) {
                    break;
                }
            }
        }
    }

    /**
     * send my host info to neighbours
     */
    public void sendMyHostInfo() {
        Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_NODE_INFO, nodeServer.getNodeInfo());
        sendMessage(msg, 3);
    }

    /**
     *
     * react when received the nodeInfo of other nodes, print the info
     *
     * @param nodeInfo the nodeInfo of the node which has sent me a message
     */
    public void processNodeInfoReceived(NodeInfo nodeInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------------------\n");
        sb.append("Message received.\n");
        sb.append("Hostname/IP:Port = " + nodeInfo.getHostname() + ":" + nodeInfo.getPort()+"\n");
        sb.append("------------------------------------------------------------");
        Logger.log(sb.toString());
    }

    /**
     * start sharing the rumor
     *
     * @param rumorType the type of rumor which u want to share
     */
    public void initateSharingRumor(int rumorType) {
    }

    /**
     * react when received the rumor, share it
     *
     * @param senderId id of the node which has sent the rumor
     */
    public void processRumorReceived(String senderId) {
        System.out.println("Received rumor from node " + senderId);
        // do i already believe the rumor?
        if (!rumor.canBeTrusted()) {
            rumor.receivedRumor();
            Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_RUMOR, null);
            sendMessage(msg, senderId);
        } else {
            System.out.println("Believes the rumor");
        }
    }

    /**
     * react when received the rumor, share it
     *
     * @param senderId id of the node which has sent the rumor
     */
    public void processRumorReceived1(String senderId) {
        System.out.println("Received rumor from node " + senderId);
        if (!rumor.canBeTrusted()) {
            rumor.receivedRumor();
            Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_RUMOR_ONE, null);
            sendMessage(msg, 2, senderId);
        } else {
            System.out.println("Believes the rumor");
        }
    }

    /**
     * react when received the rumor, share it
     *
     * @param senderId id of the node which has sent the rumor
     */
    public void processRumorReceived2(String senderId) {
        System.out.println("Received rumor from node " + senderId);
        if (!rumor.canBeTrusted()) {
            rumor.receivedRumor();
            Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_RUMOR_TWO, null);
            int limit = neighbours.size() - 2;
            if (limit < 0) {
                limit = 0;
            }
            sendMessage(msg, limit, senderId);
        } else {
            System.out.println("Believes the rumor");
        }
    }

    /**
     * react when received the rumor, share it
     *
     * @param senderId id of the node which has sent the rumor
     */
    public void processRumorReceived3(String senderId) {
        System.out.println("Received rumor from node " + senderId);
        if (!rumor.canBeTrusted()) {
            rumor.receivedRumor();
            Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_RUMOR_TWO, null);
            int limit = (neighbours.size() - 1)/2;
            if (limit < 0) {
                limit = 0;
            }
            sendMessage(msg, limit, senderId);
            sendMessage(msg, senderId);
        } else {
            System.out.println("Believes the rumor");
        }
    }

    /**
     * shuts down the node and tels all neighbours to shut down
     */
    public void initateShuttingDownAllNodes() {
        Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_SHUTDOWN_NODES, null);
        sendMessage(msg);
        System.out.println("------------------------------------------------------------");
        System.out.println("Shutting down node in 3 sec and telling naighbours to shut down");
        System.out.println("------------------------------------------------------------");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.err(ex.getMessage());
        }
        shutdown();
    }

    /**
     * react when received a message from an other node to shut down, tell neighbours to shut down too
     *
     * @param senderID the id of the node which has sent the message
     */
    public void processNodesShutdown(String senderID) {
        Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_SHUTDOWN_NODES, null);
        sendMessage(msg, senderID);
        System.out.println("------------------------------------------------------------");
        System.out.println("Shutting down node in 3 sec and telling naighbours to shut down");
        System.out.println("------------------------------------------------------------");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.err(ex.getMessage());
        }
        shutdown();
    }

    /**
     * prints a list of neighbour
     */
    public void printNeighbours() {
        System.out.println("##################   Neighbours   ###########################");
        for (NodeInfo nodeInfo : neighbours) {
            System.out.println("ID: " + nodeInfo.getId() + ", "
                    + nodeInfo.getHostname() + ":"
                    + nodeInfo.getPort());
        }
        System.out.println("############################################################");
    }

    /**
     * prints own host info
     */
    public void printHostInfo() {
        System.out.println(nodeServer.getNodeInfo().toString());
    }

    public void shutdown(){
        nodeServer.stop();
    }

}
