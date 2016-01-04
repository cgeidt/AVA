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

    private int moneyTotal;
    private int leaderMoney;
    private int followerMoney;
    private int gameState;

    public Game(int moneyTotal, int leaderMoney, int followerMoney){
        if(leaderMoney + followerMoney != moneyTotal){
            throw new RuntimeException("leader + follower are different with total amount of money");
        }
        this.moneyTotal = moneyTotal;
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

    public int getMoneyTotal() {
        return moneyTotal;
    }
}
