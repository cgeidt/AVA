package htw.ava.communication.massages;

import htw.ava.NodeManager;

import java.io.Serializable;

/**
 * Created by cgeidt on 30.12.2015.
 */
public class Game implements Serializable {

    public final static int GAME_STATE_REQUEST = 0;
    public final static int GAME_STATE_ACCEPTED = 1;
    public final static int GAME_STATE_DENIED = 2;
    public final static int GAME_STATE_NO_FOLLOWER= 3;

    private int leaderMoney;
    private int followerMoney;
    private int gameState;

    public Game(int leaderMoney, int followerMoney){

        this.leaderMoney= leaderMoney;
        this.followerMoney = followerMoney;
        this.gameState = GAME_STATE_REQUEST;
    }

    public void accept(){
        gameState = GAME_STATE_ACCEPTED;
    }

    public void deny(){
        gameState = GAME_STATE_DENIED;
    }
    public int getGameState(){
        return gameState;
    }

    public int getLeaderMoney() {
        return leaderMoney;
    }

    public int getFollowerMoney() {
        return followerMoney;
    }


    public void receivedByANonFollower(){
        gameState = GAME_STATE_NO_FOLLOWER;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("GameState: ").append(gameState).append("\n");
        sb.append("LeaderMoney: ").append(leaderMoney).append("\n");
        sb.append("FollowerMoney: ").append(followerMoney).append("\n");
        return sb.toString();
    }
}
