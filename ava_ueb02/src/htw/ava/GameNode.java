package htw.ava;

import htw.ava.communication.NodeInfo;
import htw.ava.communication.massages.Game;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by cgeidt on 06.01.2016.
 */
public abstract class GameNode extends Node {

    public GameNode(NodeInfo nodeInfo, ArrayList<NodeInfo> neighbours) throws IOException, InterruptedException {
        super(nodeInfo, neighbours);
    }

    public abstract void play();
    public abstract void processGame(Game game, String senderId);
}

