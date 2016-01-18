package htw.ava;

import htw.ava.communication.Message;
import htw.ava.communication.NodeInfo;
import htw.ava.communication.massages.Game;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class LeaderNode extends GameNode {
    private static final int AMOUNT_OF_NODES_TO_CONTACT = 5;
    private int moneyLeader;
    private int moneyFollower;
    private int money;
    private int playCounter;
    private int angenommen;
    private int abgelehnt;
    private boolean play;

    public LeaderNode(NodeInfo nodeInfo, ArrayList<NodeInfo> neighbours, int moneyLeader, int moneyFollower) throws IOException, InterruptedException {
        super(nodeInfo, neighbours);
        this.moneyFollower = moneyFollower;
        this.moneyLeader = moneyLeader;
        this.playCounter = 0;
        this.abgelehnt = 0;
        this.angenommen = 0;
        play = false;
    }


    public void processGame(Game game, String senderId){
        switch (game.getGameState()){
            case Game.GAME_STATE_REQUEST:
                game.receivedByANonFollower();
                NodeManager.logger.debug("Got request but I am a Leader");
                Message msg = new Message(nodeInfo.getId(), Message.TYPE_APPLICATION_GAME, game);
                sendMessageToNode(senderId, msg);
                play = true;
                play();
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
                msg = new Message(nodeInfo.getId(), Message.TYPE_APPLICATION_GAME, game);
                sendMessageToRandomNode(msg);
                break;
            default:
                throw new RuntimeException("Invalid GameState: "+game.getGameState());
        }
    }

    public void play(){
        if(play) {
            Game game = new Game(moneyLeader, moneyFollower);
            Message msg = new Message(nodeInfo.getId(), Message.TYPE_APPLICATION_GAME, game);
            sendMessageToRandomNodes(msg, AMOUNT_OF_NODES_TO_CONTACT);
            playCounter += AMOUNT_OF_NODES_TO_CONTACT;
        }
    }

    @Override
    public void handleMessage(Message msg, ObjectOutputStream answerStream) throws IOException {
        //Matching the message type to know which operation the node should do
        switch (msg.getType()) {
            case Message.TYPE_APPLICATION_NODE_INFO:
                processNodeInfoReceived((NodeInfo) msg.getData());
                break;
            case Message.TYPE_APPLICATION_GAME:
                processGame((Game) msg.getData(), msg.getSenderId());
                break;
            case Message.TYPE_APPLICATION_START_PLAYING:
                NodeManager.logger.log("started playing");
                play = true;
                play();
                break;
            case Message.TYPE_APPLICATION_STOP_PLAYING:
                System.out.println("PlayCounter: " +playCounter);
                NodeManager.logger.log("stopped playing");
                play = false;
                break;
            case Message.TYPE_APPLICATION_TELL_MONEY:
                Message msgAnswer = new Message(nodeInfo.getId(), Message.TYPE_APPLICATION_GAME, money);
                answerStream.writeObject(msgAnswer);
                break;
            default:
                NodeManager.logger.err(Message.UNKNOWN_TYPE+": "+msg.getType());
                break;
        }
        answerStream.close();
    }

}
