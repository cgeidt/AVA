package htw.ava.graph;


import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import htw.ava.communication.NodeInfo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Class with utils to manage the graph and get information about the graph
 */
public class GraphManager {

    private static final String JSON_ID = "id";
    private static final String JSON_HOSTNAME = "hostname";
    private static final String JSON_PORT = "port";

    private final String fileHosts;

    /**
     *
     * @param fileHosts file with conectivity infoormation about the hosts
     */
    public GraphManager(String fileHosts) {
        this.fileHosts = fileHosts;
    }

    /**
     *
     * @param id the id of the node which neighbours you want to get
     * @return returns a list all neighbours for the given host id
     * @throws FileNotFoundException
     * @throws ParseException
     * @throws Exception
     */
    public ArrayList<NodeInfo> getHostsListForId(String id) throws Exception {

        ArrayList<NodeInfo> hosts = new ArrayList<>();
        JsonReader reader = new JsonReader(new FileReader(fileHosts));

        //parsing the host information
        JsonParser parser = new JsonParser();
        JsonArray hostsArray = parser.parse(reader).getAsJsonArray();

        //adding hosts that width different id to neighbours list
        for (JsonElement obj : hostsArray) {
            try{
                JsonObject host = obj.getAsJsonObject();
                String hostId = host.get(GraphManager.JSON_ID).getAsString();
                if (!hostId.equals(id)) {
                    hosts.add(
                            new NodeInfo(
                                    hostId,
                                    host.get(GraphManager.JSON_HOSTNAME).getAsString(),
                                    host.get(GraphManager.JSON_PORT).getAsInt()
                            )
                    );
                }
            }catch(Exception e){
                throw new JsonParseException("Fehler in der JSON-Konfigdatei: Bitte README.pdf fuer das richtige Format lesen");
            }
        }
        return hosts;
    }


    /**
     * Returns the information for the given node-id
     *
     * @param id the id of the host which connectivity information u want
     * @return NodeInfo object with all information
     * @throws FileNotFoundException
     * @throws Exception
     */
    public NodeInfo getNodeConnectivityInfoForId(String id) throws FileNotFoundException, Exception {
        //reading and parsing the hostfile
        JsonReader reader = new JsonReader(new FileReader(fileHosts));
        JsonParser parser = new JsonParser();
        JsonArray hostsArray = parser.parse(reader).getAsJsonArray();

        NodeInfo nodeInfo = null;

        //gernerating NodeInfo for id
        for (JsonElement obj : hostsArray) {
            JsonObject host = obj.getAsJsonObject();
            try{
                String hostId = host.get(GraphManager.JSON_ID).getAsString();
                if (hostId.equals(id)) {
                    nodeInfo = new NodeInfo(
                            hostId,
                            host.get(GraphManager.JSON_HOSTNAME).getAsString(),
                            Integer.valueOf(host.get(GraphManager.JSON_PORT).getAsString())
                    );
                    break;
                }
            }catch(Exception e){
                throw new JsonParseException("Fehler in der JSON-Konfigdatei: Bitte README.pdf fuer das richtige Format lesen");
            }

        }

        if (nodeInfo == null) {
            throw new Exception("No connectivity info for id found");
        }
        return nodeInfo;
    }

}


