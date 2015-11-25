package htw.ava;

/**
 * Created by cgeidt on 21.10.2015.
 */
import java.util.ArrayList;
import java.util.Scanner;

import htw.ava.communication.NodeInfo;
import htw.ava.graph.GraphManager;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class NodeManager {

    //Creating the logger
    public final static Logger logger = new Logger(false);

    public static final String SEPARATOR = "\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n";

    //Options for cli
    private static final String OPTION_ID = "id";
    private static final String OPTION_ID_MSG = "id of the host";
    private static final String OPTION_HOSTS = "hosts";
    private static final String OPTION_NEIGHBOURS_MSG = "file with list of all hosts";
    private static final String OPTION_GRAPH = "graph";
    private static final String OPTION_GRAPH_MSG = "file with graph";
    private static final String OPTION_BELIEVE_RUMOR = "rumor";
    private static final String OPTION_BELIEVE_RUMOR_MSG = "believes rumor after heard it x times";
    private static final String HELP_NAME = "Node";

    private static String hostsFile;
    private static String graphFile;
    private static String hostId;
    private static int rumorCount;


    /**
     * @param args the command line arguments
     */
    public static void start(String[] args) {
        try {

            interpretCommandLine(args);

            GraphManager graphManager = new GraphManager(hostsFile, graphFile);
            NodeInfo hostNodeInfo = graphManager.getNodeConnectivityInfoForId(hostId);
            ArrayList<NodeInfo> neighbours = graphManager.getHostsListForId(hostId);
            Node node = new Node(hostNodeInfo, neighbours, rumorCount);

            int command;
            boolean run = true;
            while (run) {
                command = readCommand();
                switch (command) {
                    case 1:
                        node.sendMyHostInfo();
                        break;
                    case 2:
                        node.printNeighbours();
                        break;
                    case 3:
                        node.initiateSharingRumor();
                        break;
                    case 4:
                        node.printHostInfo();
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 9:
                        node.initiateShuttingDownAllNodes();
                        break;
                }
                if (command == 0) {
                    run = false;
                    node.shutdown();
                }

            }

        } catch (Exception ex) {
            logger.err(ex.getMessage());
        }
    }

    /**
     * Method which interprets the command line input
     *
     * @param args the command line arguments
     */
    private static void interpretCommandLine(String[] args) {
        Options options = new Options();
        HelpFormatter formatter = new HelpFormatter();

        options.addOption(OPTION_ID, true, OPTION_ID_MSG);
        options.addOption(OPTION_HOSTS, true, OPTION_NEIGHBOURS_MSG);
        options.addOption(OPTION_GRAPH, true, OPTION_GRAPH_MSG);
        options.addOption(OPTION_BELIEVE_RUMOR, true, OPTION_BELIEVE_RUMOR_MSG);

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            hostId = cmd.getOptionValue(OPTION_ID);
            hostsFile = cmd.getOptionValue(OPTION_HOSTS);
            graphFile = cmd.getOptionValue(OPTION_GRAPH);
            rumorCount = Integer.valueOf(cmd.getOptionValue(OPTION_BELIEVE_RUMOR));
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
        System.out.print("Enter command: "
                + "\n 0: Stop Server "
                + "\n 1: (A1) Send message "
                + "\n 2: Print neighbours "
                + "\n 3: Initate sharing rumor "
                + "\n 4: Print Info"
                + "\n 9: Initate shutting down all nodes ");
        System.out.println(NodeManager.SEPARATOR);
        System.out.print("_> ");

        Scanner in = new Scanner(System.in);
        int input = in.nextInt();
        System.out.println("");
        return input;

    }

}
