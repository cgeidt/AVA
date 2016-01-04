package htw.ava.communication;

import htw.ava.Node;
import htw.ava.NodeManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class which is waiting to receive a message
 */
public class NodeServer implements Runnable {
    private ServerSocket serverSocket;
    private boolean running;
    private NodeInfo nodeInfo;
    private Node node;


    /**
     * Creates a NodeServer object
     *
     * @param id id of the own node
     * @param hostname name of the own node
     * @param port port of the own node
     * @param node reference of the node itself
     */
    public NodeServer(String id, String hostname, int port, Node node) {
        this.nodeInfo = new NodeInfo(id, hostname, port);
        this.node = node;
    }

    /**
     *
     * @return the information about the node
     */
    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    @Override
    /**
     * start listening for a message
     */
    public void run() {
        this.running = true;
        try {
            //creating listener Socket with port of the node
            serverSocket = new ServerSocket(nodeInfo.getPort());
            while (isRunning()) {
                Socket socket;
                // start listening for Messages
                socket = this.serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message receivedMessage = (Message) ois.readObject();
                receivedMessage.process(node);
            }
        } catch (IOException e) {
            NodeManager.logger.err(e.getMessage());
            stop();
        } catch (ClassNotFoundException e) {
            NodeManager.logger.err(e.getMessage());
        }

    }

    /**
     * Checks if the node is still running
     *
     * @return true if running | false if not runing
     */
    private boolean isRunning() {
        return this.running;
    }

    /**
     * Makes the node stop listening for messages
     */
    public void stop() {
        this.running = false;
        try {
            serverSocket.close();
            this.serverSocket.close();
        } catch (IOException e) {
            System.out.println("Server Stopped.");
        }

    }
}
