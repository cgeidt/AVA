package htw.ava.graph;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import htw.ava.communication.NodeInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class with utils to manage the graph and get information about the graph
 */
public class GraphManager {

    private static final String JSON_ID = "id";
    private static final String JSON_HOSTNAME = "hostname";
    private static final String JSON_PORT = "port";

    private final String fileHosts;
    private final String fileGraph;

    /**
     *
     * @param fileHosts file with conectivity infoormation about the hosts
     * @param fileGraph file which represents the graph (graphiz-fle)
     */
    public GraphManager(String fileHosts, String fileGraph) {
        this.fileHosts = fileHosts;
        this.fileGraph = fileGraph;
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
        //Reading the graphfile
        File f = new File(this.fileGraph);
        FileReader in = new FileReader(f);

        //parsing the graphfile
        Parser graphParser = new Parser();
        graphParser.parse(in);
        ArrayList<Graph> graphList = graphParser.getGraphs();

        if (graphList.size() < 1) {
            throw new Exception("No graph in graphfile found.");
        }

        Graph graph = graphList.get(0);
        ArrayList<Edge> edges = graph.getEdges();

        //getting all neighbours
        ArrayList<String> neighbours = new ArrayList<>();
        for (Edge edge : edges) {
            String sourceId = edge.getSource().getNode().getId().getId();
            String targetId = edge.getTarget().getNode().getId().getId();

            if (sourceId.equals(id)) {
                neighbours.add(targetId);
            } else if (targetId.equals(id)) {
                neighbours.add(sourceId);
            }
        }

        //reading the host information
        File file = new File(this.fileHosts);
        Scanner input;

        ArrayList<NodeInfo> hosts = new ArrayList<>();
        JsonReader reader = new JsonReader(new FileReader(fileHosts));

        //parsing the host information
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray hostsArray = parser.parse(reader).getAsJsonArray();

        //matching the hostinformation for the neighbours
        for (JsonElement obj : hostsArray) {
            JsonObject host = obj.getAsJsonObject();
            String hostId = host.get(GraphManager.JSON_ID).getAsString();
            if (neighbours.contains(hostId)) {
                if (!listContainsHost(hosts, hostId)) {
                    hosts.add(
                            new NodeInfo(
                                    host.get(GraphManager.JSON_ID).getAsString(),
                                    host.get(GraphManager.JSON_HOSTNAME).getAsString(),
                                    host.get(GraphManager.JSON_PORT).getAsInt()
                            )
                    );
                }

            }

        }
        return hosts;
    }

    /**
     * Checks if the given list contains a host
     *
     * @param hosts list of hosts
     * @param hostId host id to check
     * @return true if list contains host | false if list doesnt conatin the host
     */
    private boolean listContainsHost(ArrayList<NodeInfo> hosts, String hostId) {
        boolean returnValue = false;
        for (NodeInfo nodeInfo : hosts) {
            if (nodeInfo.getId().equals(hostId)) {
                returnValue = true;
                break;
            }
        }
        return returnValue;
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


