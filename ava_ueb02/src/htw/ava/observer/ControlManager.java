package htw.ava.observer;

import htw.ava.*;
import htw.ava.communication.NodeInfo;
import htw.ava.graph.GraphManager;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by cgeidt on 13.01.2016.
 */
public class ControlManager {
    public static final String SEPARATOR = "\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n";

    //Options for cli
    private static final String OPTION_ID = "id";
    private static final String OPTION_ID_MSG = "id of the host";
    private static final String OPTION_HOSTS = "hosts";
    private static final String OPTION_HOSTS_MSG = "file with list of all hosts";
    private static final String HELP_NAME = "Node";

    private static String hostsFile;
    private static String hostId;



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
            NodeController node = new NodeController(hostNodeInfo, neighbours);

            int command;
            boolean run = true;
            while (run) {
                command = readCommand();
                switch (command) {
                    case 1:
                        node.printHostInfo();
                        break;
                    case 2:
                        node.printNeighbours();
                        break;
                    case 3:
                        node.nodesStartPlaying();
                        break;
                    case 4:
                        node.nodesStopPlaying();
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 9:
                        break;
                }
                if (command == 0) {
                    run = false;
                    node.shutdown();
                }

            }

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
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            hostId = cmd.getOptionValue(OPTION_ID);
            hostsFile = cmd.getOptionValue(OPTION_HOSTS);


        } catch (ParseException e) {
            formatter.printHelp(HELP_NAME, options);
            NodeManager.logger.err(e.getMessage());
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
        cl.append("\n 1: Print host info");
        cl.append("\n 2: Print neighbours ");
        cl.append("\n 3: Nodes start playing ");
        cl.append("\n 4: Nodes stop playing ");
        cl.append(NodeManager.SEPARATOR);
        cl.append("_> ");
        System.out.print(cl.toString());

        Scanner in = new Scanner(System.in);
        int input = in.nextInt();
        System.out.println("");
        return input;

    }

}
