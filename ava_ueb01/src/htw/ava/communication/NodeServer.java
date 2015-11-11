package htw.ava.communication;

import htw.ava.Logger;
import htw.ava.Node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by cgeidt on 21.10.2015.
 */
public class NodeServer implements Runnable {
    private ServerSocket serverSocket;
    private boolean running;
    private NodeInfo nodeInfo;
    private Node node;


    public NodeServer(String id, String hostname, int port, Node node) {
        this.nodeInfo = new NodeInfo(id, hostname, port);
        this.node = node;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    @Override
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
            stop();
        } catch (ClassNotFoundException e) {
            Logger.err(e.getMessage());
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
     * makes the node stop listening for messages
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
