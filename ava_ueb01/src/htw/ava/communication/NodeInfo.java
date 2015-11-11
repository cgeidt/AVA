package htw.ava.communication;

import java.io.Serializable;

/**
 * Created by cgeidt on 28.10.2015.
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

    public String getId() {
        return id;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

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
