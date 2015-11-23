package htw.ava.communication.massages;

import java.io.Serializable;

/**
 * Class contains logic of message rumor
 */
public class Rumor implements Serializable {
    private int counter;
    private static final int TRUST_LEVEL = 2;

    /**
     * Creates a new rumor
     */
    public Rumor() {
        counter = 0;
    }

    /**
     * Increases the counter representing how often the rumor has been received
     *
     * @return returns if the rumor can be trusted after receiving it
     */
    public boolean receivedRumor(){
        counter++;
        return canBeTrusted();
    }

    /**
     * Checks if the node already trusts the rumor
     *
     * @return true if it is trusted | false if its not trusted
     */
    public boolean canBeTrusted(){
        return counter >= TRUST_LEVEL;
    }
}
