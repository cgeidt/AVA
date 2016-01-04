package htw.ava;

import htw.ava.communication.Message;
import htw.ava.communication.NodeClient;
import htw.ava.communication.NodeInfo;
import htw.ava.communication.NodeServer;
import htw.ava.communication.massages.Game;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that implements the complete logic of a node
 */
public class Node {
    private NodeServer nodeServer;
    private ArrayList<NodeInfo> neighbours;

    /** Variables used to realise the game */
    private static final int GAME_MODE_STATIC = 0;
    private static final int GAME_ODE_DYNAMIC = 1;

    private int money;
    private int acceptLimitLeader;
    private int acceptLimitFollower;
    private int gameMode;

    /**
     * Creates a node object using a NodeInfo object
     *
     * @param nodeInfo object which contains the information of the host node
     * @param neighbours neighbours of the node
     */
    public Node(NodeInfo nodeInfo, ArrayList<NodeInfo> neighbours) {
        money = 0;
        initNode(nodeInfo.getId(), nodeInfo.getHostname(), nodeInfo.getPort(), neighbours);
    }

    /**
     * Initiates the node
     *
     * @param id id the node will get
     * @param hostname hostname the node will get
     * @param port port the node will get
     * @param neighbours neighbours of the node
     */
    private void initNode(String id, String hostname, int port, ArrayList<NodeInfo> neighbours){
        this.neighbours = neighbours;
        startServer(id, hostname, port);
    }

    /**
     * Starts the node server logic
     *
     * @param id id the node will get
     * @param hostname hostname the node will get
     * @param port port the node will get
     */
    private void startServer(String id, String hostname, int port){
        this.nodeServer = new NodeServer(id, hostname, port, this);
        //listener thread
        Thread listenerThread = new Thread(nodeServer);
        listenerThread.start();
    }


    /**
     * send a message to all neighbours
     *
     * @param msg object which contains the message
     */
    private void sendMessage(Message msg) {
        this.sendMessage(msg, -1, "-1");
    }

    /**
     * send a message to a amount of neighbours
     *
     * @param msg object which contains the message
     * @param limit the limit how much neighbours should get a message
     */
    private void sendMessage(Message msg, int limit) {
        this.sendMessage(msg, limit, "-1");
    }

    /**
     * send message to all neighbours expect one
     *
     * @param msg object which contains the message
     * @param except the id of a neighbour which should not get the message
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
     * @param except the id of a neighbour which should not get the message
     */
    private void sendMessage(Message msg, int limit, String except) {
        int count = 0;
        for (NodeInfo nodeInfo : neighbours) {
            // checks if current node is the one which should not get the message
            if (nodeInfo.getId().equals(except)) {
                continue;
            }
            try {
                //sending message
                Thread sendThread = new Thread(new NodeClient(nodeInfo.getHostname(), nodeInfo.getPort(), msg));
                sendThread.start();
                NodeManager.logger.debug("Sent message of type " + Message.TYPE_MAPPING[msg.getType()] + " to node " + nodeInfo.getId());
            } catch (IOException ex) {
                StringBuilder sb = new StringBuilder();
                sb.append("------------------------------------------------------------");
                sb.append(ex.getMessage());
                sb.append("Could not find node with id " + nodeInfo.getId());
                sb.append("Hostname/IP:Port = " + nodeInfo.getHostname() + ":" + nodeInfo.getPort());
                sb.append("------------------------------------------------------------");
                NodeManager.logger.err(sb.toString());

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
        Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_APPLICATION_NODE_INFO, nodeServer.getNodeInfo());
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
        NodeManager.logger.log(sb.toString());
    }


    /**
     * shuts down the node and tels all neighbours to shut down
     */
    public void initiateShuttingDownAllNodes() {
        Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_COMMAND_SHUTDOWN_NODES, null);
        sendMessage(msg);
        System.out.println("------------------------------------------------------------");
        System.out.println("Shutting down node in 3 sec and telling neighbours to shut down");
        System.out.println("------------------------------------------------------------");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            NodeManager.logger.err(ex.getMessage());
        }
        shutdown();
    }

    /**
     * react when received a message from an other node to shut down, tell neighbours to shut down too
     *
     * @param senderID the id of the node which has sent the message
     */
    public void processNodesShutdown(String senderID) {
        Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_COMMAND_SHUTDOWN_NODES, null);
        sendMessage(msg, senderID);
        System.out.println("------------------------------------------------------------");
        System.out.println("Shutting down node in 3 sec and telling neighbours to shut down");
        System.out.println("------------------------------------------------------------");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            NodeManager.logger.err(ex.getMessage());
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
        System.out.println("#############################################################");
    }


    public void processGame(Game game){
        switch (game.getGameState()){
            case Game.GAME_STATE_REQUEST:

                break;
            case Game.GAME_STATE_ACCEPTED:
                break;
            case Game.GAME_STATE_DENIED:
                break;
            default:
                throw new RuntimeException("Invalid GameState");
        }
    }

    private boolean acceptGame(Game game){
        switch (gameMode){
            case GAME_MODE_STATIC:
                if(game.foll)
                break;
            case GAME_ODE_DYNAMIC:
                break;
            default:
                throw new RuntimeException("Invalid GameMode");
        }

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
