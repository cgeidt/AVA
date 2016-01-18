package htw.ava.observer;

import htw.ava.Node;
import htw.ava.NodeManager;
import htw.ava.communication.Message;
import htw.ava.communication.NodeInfo;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by cgeidt on 12.01.2016.
 */
public class NodeController extends Node {

    public NodeController(NodeInfo nodeInfo,ArrayList<NodeInfo> neighbours) throws IOException, InterruptedException {
        super(nodeInfo, neighbours);
    }

    @Override
    public void handleMessage(Message msg, ObjectOutputStream answerStream) {
        switch (msg.getType()){
            case Message.TYPE_APPLICATION_TELL_MONEY_ANSWER:
                NodeManager.logger.log("Host: "+msg.getSenderId()+", Money:"+msg.getData());
                break;
            default:
                NodeManager.logger.err("Invalid MessageType for NodeController");
        }
    }

    public void nodesStopPlaying(){
        try {
            for(NodeInfo nodeInfo : neighbours) {
                Socket socket = new Socket(nodeInfo.getHostname(), nodeInfo.getPort());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = new Message(nodeInfo.getId(), Message.TYPE_APPLICATION_STOP_PLAYING, null);
                oos.writeObject(msg);
                oos.flush();
                socket.close();
            }
            Thread.sleep(2000);
            for(NodeInfo nodeInfo : neighbours){
                int money = askNodeForMoney(nodeInfo);
                NodeManager.logger.log("Host: "+nodeInfo.getId()+", Money:"+money);
            }

        } catch (Exception e) {
            NodeManager.logger.err("No connection to node \n"+e.getMessage());
        }
    }

    public void nodesStartPlaying(){
        try {
            Socket socket = new Socket(neighbours.get(0).getHostname(), neighbours.get(0).getPort());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message msg = new Message(nodeInfo.getId(), Message.TYPE_APPLICATION_START_PLAYING, null);
            oos.writeObject(msg);
            oos.flush();
            socket.close();

        } catch (Exception e){
            NodeManager.logger.err(e.getMessage());
        }
    }

    public int askNodeForMoney(NodeInfo targetNodeInfo) {
        try {
            Socket socket = new Socket(targetNodeInfo.getHostname(), targetNodeInfo.getPort());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message msg = new Message(nodeInfo.getId(), Message.TYPE_APPLICATION_TELL_MONEY, null);
            oos.writeObject(msg);
            oos.flush();
            Message response = (Message)ois.readObject();
            socket.close();
            return (Integer)response.getData();
        } catch(Exception e) {
            System.out.println("Error connecting to " + targetNodeInfo.getHostname() + ":" + targetNodeInfo.getPort());
            return -1;
        }
    }


}
