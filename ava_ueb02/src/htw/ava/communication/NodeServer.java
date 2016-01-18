package htw.ava.communication;

import htw.ava.Node;
import htw.ava.NodeManager;
import htw.ava.communication.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class which is waiting to receive a message
 */
public class NodeServer implements Runnable {
    private ServerSocket serverSocket;
    private int port;
    private boolean running;
    private Node node;


    /**
     * Creates a NodeServer object
     *
     * @param port port of the own node
     * @param node reference of the node itself
     */
    public NodeServer(int port, Node node) {
        this.port = port;
        this.node = node;
    }


    @Override
    /**
     * start listening for a message
     */
    public void run() {
        this.running = true;
        try {
            //creating listener Socket with port of the node
            serverSocket = new ServerSocket(port);
            while (isRunning()) {
                Socket socket;
                // start listening for Messages
                socket = this.serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                Message msg = (Message) ois.readObject();
                node.handleMessage(msg, oos);
            }
        } catch (IOException e) {
            e.printStackTrace();
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