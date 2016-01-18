package htw.ava;

/**
 * Created by cgeidt on 21.10.2015.
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import htw.ava.communication.NodeInfo;
import htw.ava.graph.GraphManager;
import org.apache.commons.cli.*;

public class NodeManager {

    //Creating the logger
    public final static Logger logger = new Logger(true);

    public static final String SEPARATOR = "\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n";

    //Options for cli
    private static final String OPTION_ID = "id";
    private static final String OPTION_ID_MSG = "id of the host";
    private static final String OPTION_HOSTS = "hosts";
    private static final String OPTION_HOSTS_MSG = "file with list of all hosts";
    private static final String OPTION_EXERCISE = "exercise";
    private static final String OPTION_EXERCISE_MSG = "example: --exercise <exerciseNumber> <cfgFile>";
    private static final String HELP_NAME = "Node";

    private static String hostsFile;
    private static String hostId;
    private static JsonObject exerciseParams;
    private static int exerciseNumber;


    public static void main(String[] args) {
        start(args);
    }

    /**
     * @param args the command line arguments
     */
    public static void start(String[] args) {
        try {

            interpretCommandLine(args);

            GraphManager graphManager = new GraphManager(hostsFile);
            NodeInfo hostNodeInfo = graphManager.getNodeConnectivityInfoForId(hostId);
            ArrayList<NodeInfo> neighbours = graphManager.getHostsListForId(hostId);
            GameNode node = null;
            switch (exerciseNumber){
                case 2:
                    String type = exerciseParams.get("type").getAsString();
                    if(type.equals("leader")){
                        int moneyLeader = exerciseParams.get("leader").getAsInt();
                        int moneyFollower = exerciseParams.get("follower").getAsInt();
                        node = new LeaderNode(hostNodeInfo, neighbours, moneyLeader, moneyFollower);
                    }else{
                        int accept = exerciseParams.get("accept").getAsInt();
                        node = new FollowerNode(hostNodeInfo, neighbours, accept);
                    }
                    break;
                default:
                    throw new Exception("Unknown exercise number");
            }

            node.startServer();

        } catch (Exception ex) {
           ex.printStackTrace();
        }
    }

    /**
     * Method which interprets the command line input
     *
     * @param args the command line arguments
     */
    private static void interpretCommandLine(String[] args) throws Exception {
        Options options = new Options();
        HelpFormatter formatter = new HelpFormatter();

        options.addOption(OPTION_ID, true, OPTION_ID_MSG);
        options.addOption(OPTION_HOSTS, true, OPTION_HOSTS_MSG);
        Option exOption = new Option(OPTION_EXERCISE, true, OPTION_EXERCISE_MSG);
        exOption.setArgs(2);
        exOption.setValueSeparator(' ');
        exOption.setRequired(true);
        options.addOption(exOption);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            hostId = cmd.getOptionValue(OPTION_ID);
            hostsFile = cmd.getOptionValue(OPTION_HOSTS);
            String[] exercise = cmd.getOptionValues(OPTION_EXERCISE);

            String cfgFile = exercise[1];
            exerciseNumber = Integer.valueOf(exercise[0]);
            exerciseParams = getExerciseParams(cfgFile, exerciseNumber);

        } catch (ParseException e) {
            formatter.printHelp(HELP_NAME, options);
            logger.err(e.getMessage());
        }
    }

    /**
     * Method which reads the command nd prints the command list
     *
     * @return returns the selected command
     */
    private static int readCommand() {
        System.out.println(NodeManager.SEPARATOR);
        StringBuilder cl = new StringBuilder("Enter command: ");
        cl.append("\n 0: Stop Server ");
        System.out.print(cl.toString());

        Scanner in = new Scanner(System.in);
        int input = in.nextInt();
        System.out.println("");
        return input;

    }

    private static JsonObject getExerciseParams(String cfgFile, int exercise) throws Exception {
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(cfgFile));
        } catch (FileNotFoundException e) {
            logger.err(e.getMessage());
        }

        //parsing the host information
        JsonParser parser = new JsonParser();
        JsonArray cfgArray = parser.parse(reader).getAsJsonArray();

        for (JsonElement obj : cfgArray) {
            try {
                JsonObject jsonExercises = obj.getAsJsonObject();
                int ex = jsonExercises.get("exercise").getAsInt();
                if(ex == exercise){
                    JsonArray nodes = jsonExercises.get("nodes").getAsJsonArray();
                    for (JsonElement nodeElement : nodes) {
                        JsonObject node = nodeElement.getAsJsonObject();
                        if(node.get("id").getAsString().equals(hostId)){
                            return node.get("params").getAsJsonObject();
                        }
                    }
                }

            } catch (Exception e) {
                throw new JsonParseException("Fehler in der JSON-Konfigdatei: Bitte README.pdf fuer das richtige Format lesen");
            }
        }
        throw new Exception("No exercise and hostId in cfg file found");
    }


}
