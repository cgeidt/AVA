package htw.ava.communication;

import htw.ava.NodeManager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class which is responsible for sending the messages
 */
public class NodeClient implements Runnable {

    private Message msg;
    private Socket socket;

    /**
     *
     * Creates and NodeClient
     *
     * @param targetHostname hostname of the target
     * @param targetPort port of the target
     * @param msg message to send to the target
     * @throws IOException
     */
    public NodeClient(String targetHostname, int targetPort, Message msg) throws IOException {
        this.socket = new Socket(targetHostname, targetPort);
        this.msg = msg;
    }

    @Override
    /**
     * Starts sending the message
     */
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(this.msg);
            oos.close();
            this.socket.close();
        } catch (IOException e) {
            NodeManager.logger.err(e.getMessage());
        }
    }
}
