package htw.ava.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by cgeidt on 27.10.2015.
 */
public class NodeClient implements Runnable {

    private Message msg;
    private Socket socket;

    public NodeClient(String targetHostname, int targetPort, Message msg) throws IOException {
        this.socket = new Socket(targetHostname, targetPort);
        this.msg = msg;
    }

    @Override
    public void run() {

        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(this.msg);
            oos.close();
            this.socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
