package htw.ava;

import htw.ava.communication.massages.Game;

/**
 * Created by cgeidt on 05.01.2016.
 */
public class GameException extends Exception {
    public GameException(String msg){
        super(msg);
    }

    public GameException(){
        super();
    }
}
