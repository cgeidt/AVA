package htw.ava;

import htw.ava.communication.Message;
import htw.ava.communication.NodeInfo;
import htw.ava.communication.massages.Game;

import java.util.ArrayList;

/**
 * Created by cgeidt on 06.01.2016.
 */
public class FollowerNode extends GameNode{
    public static final int AMOUNT_OF_NODES_TO_CONTACT = 3;

    private int money;
    private int minToAccept;

    public FollowerNode(NodeInfo nodeInfo, ArrayList<NodeInfo> neighbours, int minToAccept){
        super(nodeInfo, neighbours);
        this.minToAccept = minToAccept;
    }


    public void processGame(Game game, String senderId){
        switch (game.getGameState()){
            case Game.GAME_STATE_REQUEST:
                boolean isAccepted;
                if(isAccepted = (game.getFollowerMoney() >= minToAccept)){
                    money += game.getFollowerMoney();
                    game.accept();
                }
                NodeManager.logger.debug("Request: "+game.getLeaderMoney()+"|"+game.getFollowerMoney()+"\nFrom: "+senderId+"\nAccepted: "+isAccepted);
                Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_APPLICATION_GAME, game);
                sendMessageToNode(senderId, msg);
                NodeManager.logger.debug("Sent answer");
                NodeManager.logger.log("Money: " + money);
                break;
            default:
                throw new RuntimeException("Invalid GameState: "+game.getGameState());
        }
    }

    public void play(){
        NodeManager.logger.log("I am a follower, i cant start games!");
    }

    @Override
    public void handleMessage(Message msg) {
        //Matching the message type to know which operation the node should do
        switch (msg.getType()) {
            case Message.TYPE_APPLICATION_NODE_INFO:
                processNodeInfoReceived((NodeInfo) msg.getData());
                break;
            case Message.TYPE_APPLICATION_GAME:
                processGame((Game) msg.getData(), msg.getSenderId());
                break;
            case Message.TYPE_COMMAND_SHUTDOWN_NODES:
                processNodesShutdown(msg.getSenderId());
                break;
            default:
                NodeManager.logger.err(Message.UNKNOWN_TYPE+": "+msg.getType());
                break;
        }
    }

}
