package htw.ava.communication;

import htw.ava.NodeManager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class which is responsible for sending the messages
 */
public class NodeClient implements Runnable {

    private String targetHostname;
    private int targetPort;
    private boolean isConnected;
    private ObjectOutputStream oos;

    /**
     *
     * Creates and NodeClient
     *
     * @param targetHostname hostname of the target
     * @param targetPort port of the target
     * @throws IOException
     */
    public NodeClient(String targetHostname, int targetPort){
        this.targetPort = targetPort;
        this.targetHostname = targetHostname;
    }

    @Override
    /**
     * Starts sending the message
     */
    public void run() {
        try {
            Socket socket = new Socket(targetHostname, targetPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            isConnected = true;
        }catch(Exception e){
            e.printStackTrace();
            try {
                NodeManager.logger.err("Trying to reconnect to "+targetHostname+":"+targetPort);
                Thread.sleep(1000);
            }catch(Exception ignored){}
            isConnected = false;
            run();
        }
    }

    public void send(Message msg){
        boolean isSend = false;
        while(!isSend){
            if(!isConnected) {
                System.out.println("Not ready for Transmitting!");
                try {
                    Thread.sleep(1000);
                }catch(Exception ignored){}
                run();
            }
            try {
                oos.writeObject(msg);
                isSend = true;
            }catch (Exception e){
                isConnected = false;
                run();
            }
        }
    }
}
