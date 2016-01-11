package htw.ava;

import htw.ava.communication.Message;
import htw.ava.communication.NodeInfo;
import htw.ava.communication.massages.Game;

import java.util.ArrayList;

/**
 * Created by cgeidt on 06.01.2016.
 */
public class LeaderNode extends GameNode {
    private int AMOUNT_OF_NODES_TO_CONTACT = 3;
    private ArrayList<Integer> followers;
    private int moneyLeader;
    private int moneyFollower;
    private int money;

    public LeaderNode(NodeInfo nodeInfo, ArrayList<NodeInfo> neighbours, int moneyLeader, int moneyFollower){
        super(nodeInfo, neighbours);
        this.moneyFollower = moneyFollower;
        this.moneyLeader = moneyLeader;
        followers = new ArrayList<Integer>();
    }


    public void processGame(Game game, String senderId){
        switch (game.getGameState()){
            case Game.GAME_STATE_REQUEST:
                game.receivedByANonFollower();
                NodeManager.logger.debug("Got request but I am a Leader");
                Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_APPLICATION_GAME, game);
                sendMessageToNode(senderId, msg);
                break;
            case Game.GAME_STATE_ACCEPTED:
                NodeManager.logger.debug("Host "+senderId+" accepted request");
                money += game.getLeaderMoney();
                NodeManager.logger.log("Money: " + money);
                break;
            case Game.GAME_STATE_DENIED:
                break;
            case Game.GAME_STATE_NO_FOLLOWER:
                game = new Game(moneyLeader, moneyFollower);
                msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_APPLICATION_GAME, game);
                sendMessageToRandomNode(msg);
                break;
            default:
                throw new RuntimeException("Invalid GameState: "+game.getGameState());
        }
    }

    public void play(){
        for(int i = 0; i < AMOUNT_OF_NODES_TO_CONTACT; i++){
            Game game = new Game(moneyLeader, moneyFollower);
            Message msg = new Message(nodeServer.getNodeInfo().getId(), Message.TYPE_APPLICATION_GAME, game);
            sendMessageToRandomNode(msg);
        }
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
