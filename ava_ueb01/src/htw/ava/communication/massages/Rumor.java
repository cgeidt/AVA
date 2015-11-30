package htw.ava.communication.massages;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class contains logic of message rumor
 */
public class Rumor implements Serializable {
    private int counter;
    private int trustLevel;
    private ArrayList<String> heardRumorFrom;
    private boolean isShared;

    /**
     * Creates a new rumor
     *
     * @param trustLevel believes rumor after x times
     */
    public Rumor(int trustLevel) {
        this.heardRumorFrom = new ArrayList<String>();
        this.trustLevel = trustLevel;
        this.isShared = false;
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
        return counter >= trustLevel;
    }

    /**
     *
     * @param id
     * @return if the rumor is heard from a given neighbour
     */
    public boolean heardFromNeighbour(String id){
       return  heardRumorFrom.contains(id);
    }

    /**
     *
     * @return if the rumor got shared with the neighbours already
     */
    public boolean isShared(){
        return isShared;
    }

    /**
     * Sets the rumor to the state shared
     */
    public void shared(){
        this.isShared = true;
    }
}
