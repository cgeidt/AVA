package htw.ava.communication;

import java.io.Serializable;

/**
 * Class contains information about a node
 */
public class NodeInfo implements Serializable {
    private String id;
    private String hostname;
    private int port;

    public NodeInfo(String id, String hostname, int port) {
        this.id = id;
        this.hostname = hostname;
        this.port = port;
    }

    /**
     *
     * @return the id of the node
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return the hostname of the node
     */
    public String getHostname() {
        return hostname;
    }

    /**
     *
     * @return the port of the node
     */
    public int getPort() {
        return port;
    }

    /**
     *
     * @return return the inforation about the node as a string
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("##################   HostInfo    ###########################").append("\n");
        sb.append("ID: ").append(getId()).append("\n");
        sb.append("Hostname: ").append(getHostname()).append("\n");
        sb.append("Port: ").append(getPort()).append("\n");
        sb.append("############################################################").append("\n");
        return sb.toString();
    }
}
