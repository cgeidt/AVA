/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphgen;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;
import org.apache.commons.cli.*;

/**
 *
 * @author cgeidt
 */
public class GraphGen {

    
    private static final String OPTION_EDGES = "edges";
    private static final String OPTION_EDGES_MSG = "count of edges, required, edges > nodes";
    private static final String OPTION_NODES = "nodes";
    private static final String OPTION_NODES_MSG = "count of node, rquired";
    private static final String OPTION_OUTPUT = "output";
    private static final String OPTION_OUTPUT_MSG = "filepath of outputfile, required";
    private static final String HELP_NAME = "GraphGen";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    Options options = new Options();
        HelpFormatter formatter = new HelpFormatter();

        options.addOption(OPTION_EDGES, true, OPTION_EDGES_MSG);
        options.addOption(OPTION_NODES, true, OPTION_NODES_MSG);
        options.addOption(OPTION_OUTPUT, true, OPTION_OUTPUT_MSG);

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            int edgeCount = Integer.valueOf(cmd.getOptionValue(OPTION_EDGES));
            int nodeCount = Integer.valueOf(cmd.getOptionValue(OPTION_NODES));
            String outputFile = cmd.getOptionValue(OPTION_OUTPUT);
            
            if((nodeCount < edgeCount) || (outputFile != null) ){
                String graph = generateGraph(nodeCount, edgeCount);
                saveGraph(graph, outputFile);
            }else{
                formatter.printHelp(HELP_NAME, options);
            }
            
                      
        }catch(NumberFormatException | ParseException e){
            formatter.printHelp(HELP_NAME, options);
        }catch(IllegalArgumentException | FileNotFoundException e){
            System.err.println(e.getMessage());
        }
    }
    
    private static String generateGraph(int nodeCount, int edgeCount){
        boolean edgesArray[][] = new boolean[nodeCount+1][nodeCount+1];
        Random rand = new Random();

        StringBuilder graphString = new StringBuilder("graph G {\n");
        int counter = 0;
        for(int i = 2; i <= nodeCount; i++){  
            int randomNode = rand.nextInt(i-1) + 1;
            if(!edgeExists(edgesArray, i, randomNode)){
                edgesArray[i][randomNode] = true;
                graphString.append(i).append(" -- ").append(randomNode).append(";\n");
                counter++;
            }
        }
        while(counter < edgeCount){
            int rN1 = rand.nextInt(nodeCount) + 1;
            int rN2 = rand.nextInt(nodeCount) + 1;
            if(!edgeExists(edgesArray, rN1, rN2)){
                edgesArray[rN1][rN2] = true;
                graphString.append(rN1).append(" -- ").append(rN2).append(";\n");
                counter++;
            }
            
        }
        graphString.append("}");
        
        return graphString.toString();
        
    } 
    
    private static boolean edgeExists(boolean edgesArray[][], int rN1, int rN2){
        return (edgesArray[rN1][rN2] || edgesArray[rN2][rN1]) || (rN1 == rN2);
    }
    
    private static void saveGraph(String graph, String fileName) throws FileNotFoundException{
        PrintStream out = new PrintStream(new FileOutputStream(fileName));
        out.print(graph);
        System.out.println("Created Graph");
    }
    
}
