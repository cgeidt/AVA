package htw.ava;

import htw.ava.communication.Message;
import htw.ava.communication.NodeClient;
import htw.ava.communication.NodeInfo;
import htw.ava.communication.NodeServer;
import htw.ava.communication.massages.Game;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Class that implements the complete logic of a node
 */
public abstract class Node {
    protected NodeInfo nodeInfo;
    protected ArrayList<NodeInfo> neighbours;
    protected HashMap<String, Thread> connectionThreads;
    protected HashMap<String, NodeClient> nodeClients;
    protected ServerSocket serverSocket;
    protected boolean runNode;

    /**
     * Creates a node object using a NodeInfo object
     *
     * @param nodeInfo object which contains the information of the host node
     * @param neighbours neighbours of the node
     */
    public Node(NodeInfo nodeInfo, ArrayList<NodeInfo> neighbours) throws IOException, InterruptedException {
        this.serverSocket = new ServerSocket(nodeInfo.getPort());
        this.nodeInfo = nodeInfo;
        this.neighbours = neighbours;
        connectionThreads = new HashMap<String, Thread>();
        nodeClients = new HashMap<String, NodeClient>();
    }



    private void connect(){
        for (NodeInfo nodeInfo: neighbours){
            String id = nodeInfo.getId();
            NodeClient nodeClient = new NodeClient(nodeInfo.getHostname(), nodeInfo.getPort());
            nodeClients.put(id, nodeClient);
            Thread connectionThread = new Thread(nodeClient);
            connectionThreads.put(id, connectionThread);
            connectionThread.start();
        }
    }


    public void startServer() throws IOException, InterruptedException {
        NodeServer nodeServer = new NodeServer(nodeInfo.getPort(),this);
        new Thread(nodeServer).start();
        runNode = true;
        while(runNode){
            Thread.sleep(1000);
        }

    }


    protected void sendMessageToRandomNodes(Message msg, int count){
        count = (count < 0) ? 0 : count;
        for(int i = 0; i < count; i++){
            sendMessageToRandomNode(msg);
        }
    }

    protected void sendMessageToRandomNode(Message msg){
        Random random = new Random();
        int rndIndex = random.nextInt(neighbours.size());
        sendMessageToNode(neighbours.get(rndIndex).getId(),msg);
    }

    protected void sendMessageToAllNodes(Message msg){
        for(NodeInfo nodeInfo : neighbours){
            sendMessageToNode(nodeInfo.getId(), msg);
        }
    }

    protected void sendMessageToNode(String id, Message msg){
        nodeClients.get(id).send(msg);
    }

    /**
     *
     * react when received the nodeInfo of other nodes, print the
     *info
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


    abstract public void handleMessage(Message msg, ObjectOutputStream answerStream) throws IOException;

    /**
     * prints own host info
     */
    public void printHostInfo() {
        System.out.println(nodeInfo.toString());
    }

    public void shutdown() throws IOException {
        serverSocket.close();
    }


}
