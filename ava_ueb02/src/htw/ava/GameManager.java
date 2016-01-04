package htw.ava;

import htw.ava.communication.massages.Game;

public class GameManager {
    public static final int GAME_MODE_STATIC = 0;
    public static final int GAME_ODE_DYNAMIC = 1;

    private int money;
    private int minToAccept;
    private int gameMode;

    public GameManager(int minToAccept, int gameMode){
        money = 0;
        this.minToAccept = minToAccept;
        this.gameMode = gameMode;
    }

    public void accepted(Game game){
        money += game.getLeaderMoney();
    }

    public boolean play(Game game){
        boolean accept = isAcceptable(game);
       if(accept){
           money += game.getFollowerMoney();
           game.accept();
       }
        return accept;
    }


    public boolean isAcceptable(Game game){
        switch (gameMode){
            case GAME_MODE_STATIC:
                if(game.getFollowerMoney() >= minToAccept){
                    return true;
                }else{
                    return false;
                }
            case GAME_ODE_DYNAMIC:
                if(game.getMoneyTotal()/2 >= game.getFollowerMoney()){
                    return true;
                }else{
                    return false;
                }
            default:
                throw new RuntimeException("Invalid GameMode");
        }

    }

    public int getMoney() {
        return money;
    }
}
