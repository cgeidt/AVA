package htw.ava.communication.massages;

import java.io.Serializable;

/**
 * Created by cgeidt on 27.10.2015.
 */
public class Rumor implements Serializable {
    private int counter;
    private static final int TRUST_LEVEL = 2;


    public Rumor() {
        counter = 0;
    }

    /**
     * Increases the counter representing how often the rumor has been received
     */
    public void receivedRumor(){
        counter++;
    }

    /**
     * Checks if the node already trusts the rmor
     *
     * @return true if it is trusted | false if its not trusted
     */
    public boolean canBeTrusted(){
        return counter >= TRUST_LEVEL;
    }
}
