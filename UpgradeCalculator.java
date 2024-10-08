/*
 * COMP2230 - Algorithms
 * Assignment 2 - Answers
 * @author  Studious Student - c1234567 (Replace with your name and student number)
 * @version 1.0
 * 
 * This is where you will write your code for the assignment. The required methods have been started for you, you may add additional helper methods and classes as required.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

class GraphNode {
    // the node id
    public int Id;
    public boolean selected = false;

    /**
     * constructor
     * 
     * @param nodeId the node id
     */
    GraphNode(int nodeId) {
        Id = nodeId;
    }
}

class NodeValue {
    public int nodeID;
    public double value;

    NodeValue(int id, double val) {
        nodeID = id;
        value = val;
    }
}

class GraphEdge {
    public int source;
    public int target;
    public double weight;

    GraphEdge(int intersection1ID, int intersection2ID, double nodeWeight) {
        source = intersection1ID;
        target = intersection2ID;
        weight = nodeWeight;
    }
}

class Graph {
    // list of nodes
    public List<GraphNode> nodeList;

    // list of adjacency lists
    public List<List<GraphEdge>> adjList;

    /**
     * constructor for unweighted graph
     * 
     * @param nodeCount the number of nodes, indexex 0 to nodeCount - 1
     * @param adjMatrix the adjacency matrix for the graph
     */
    public Graph(int nodeCount, Double[][] adjMatrix) {
        nodeList = new ArrayList<GraphNode>();
        adjList = new ArrayList<List<GraphEdge>>();

        for (int id = 0; id < nodeCount; id++) {
            nodeList.add(new GraphNode(id));
            adjList.add(new ArrayList<GraphEdge>());
        }

        for (int i = 0; i < nodeCount; i++)
            for (int j = 0; j < nodeCount; j++)
                if (adjMatrix[i][j] > 0)
                    adjList.get(i).add(new GraphEdge(i, j, adjMatrix[i][j]));
    }

    public void selectNode(int nodeId) {
        nodeList.get(nodeId).selected = true;
        for (int i = 0; i < adjList.get(nodeId).size(); i++) {
            GraphEdge edge = adjList.get(nodeId).get(i);
            nodeList.get(edge.target).selected = true;
        }
    }
}

public class UpgradeCalculator {
    private final MapGenerator mapGen;
    public String[] upgradeData;
    public UpgradeCheck checker;
    public String cityMap = null;
    private Map<String, Integer> roadStringToIndex = new HashMap<>(); // Map of road names to indices
    private Map<Integer, String> roadIndexToString = new HashMap<>(); // Map of road indices to names
    private Map<String, Integer> intersectionStringToIndex = new HashMap<>(); // Map of intersection names to indices
    private Map<Integer, String> intersectionIndexToString = new HashMap<>(); // Map of intersection indices to names
    private Map<String, String> endPointToRoadName = new HashMap<>(); // Map of concatenated endpoints to road names
    private Map<String, List<GraphEdge>> roadMap = new HashMap<>(); // Map of road names to road nodes
    private Graph graph; // Graph object to store the city map
    private double congestion = 0.0; // Stores the congestion of the city map

    public UpgradeCalculator(int seed) {
        mapGen = new MapGenerator(seed); // Pass a seed to the random number generator, allows for reproducibility
    }

    /*
     * This method must load the city map from the map generator and store it
     * You may assume that all marking scripts will call this method before any
     * others.
     * Do not modify the code above the comment line in this method - this will be
     * used to manually insert specific maps for marking purposes.
     * Write your code below the comment line where indicated.
     */
    public void loadMap() {
        if (cityMap == null) {
            cityMap = mapGen.generateMap();
        }
        // Write your code below this line - just copy from your assignment 1 solution,
        // it's the same map generator
        // Split the cityMap string into individual road strings
        String[] roads = cityMap.split("}, \\{");

        // Iterate over each road string
        for (String road : roads) {
            // Removes the curly braces from the string
            road = road.replace("{", "").replace("}", "");

            // Splits the string into each of the components that are seperated by ", "
            int firstComma = road.indexOf(", ");
            String roadName = road.substring(0, firstComma);

            int secondComma = road.indexOf(", ", firstComma + 2);
            String endPoint1 = road.substring(firstComma + 2, secondComma);

            int thirdComma = road.indexOf(", ", secondComma + 2);
            String endPoint2 = road.substring(secondComma + 2, thirdComma);

            String weight = road.substring(thirdComma + 2);

            List<GraphEdge> roadNodes = new ArrayList<>();

            // Stores the road name as an index and the road name as a string allowing for
            // it to be accessed in multiple ways
            roadStringToIndex.put(roadName, roadStringToIndex.size());
            roadIndexToString.put(roadIndexToString.size(), roadName);

            // Map endpoints to indices and indices to endpoints for easy access
            if (!intersectionStringToIndex.containsKey(endPoint1)) {
                intersectionStringToIndex.put(endPoint1, intersectionStringToIndex.size());
                intersectionIndexToString.put(intersectionIndexToString.size(), endPoint1);
            }
            if (!intersectionStringToIndex.containsKey(endPoint2)) {
                intersectionStringToIndex.put(endPoint2, intersectionStringToIndex.size());
                intersectionIndexToString.put(intersectionIndexToString.size(), endPoint2);
            }

            // Creates a new GraphEdge object with the source, target and weight of the road
            roadNodes.add(new GraphEdge(intersectionStringToIndex.get(endPoint1),
                    intersectionStringToIndex.get(endPoint2), Double.parseDouble(weight)));

            // Map the road name to the list of road nodes
            roadMap.put(roadName, roadNodes);

            // Maps the concatenated string of endpoints to the road name that they are
            // apart of for easy access
            endPointToRoadName.put(
                    intersectionStringToIndex.get(endPoint1) + "" + intersectionStringToIndex.get(endPoint2), roadName);
        }

        // Stores the total number of endpoints in the map
        int endPointCount = intersectionIndexToString.size();

        // Initializes a new 2D array to store the graph matrix
        Double[][] graphMatrix = new Double[endPointCount][endPointCount];

        // Initializes the graphMatrix with 0.0
        for (int i = 0; i < endPointCount; i++) {
            Arrays.fill(graphMatrix[i], 0.0);
        }
        // Stores the roadMap data in the graphMatrix
        for (int i = 0; i < roadMap.size(); i++) {
            List<GraphEdge> edges = roadMap.get(roadIndexToString.get(i));
            if (edges != null && !edges.isEmpty()) {
                GraphEdge edge = edges.get(0);
                if (graphMatrix[edge.source][edge.target] == 0.0) {
                    graphMatrix[edge.source][edge.target] = edge.weight;
                    graphMatrix[edge.target][edge.source] = edge.weight;
                }
            }
        }

        // creates a new Graph object with the number of endpoints and the graphMatrix
        graph = new Graph(endPointCount, graphMatrix);
    }

    /*
     * @param budgetLimit the maximum amount of money that can be spent on road
     * upgrades
     * 
     * @param timeLimit the maximum amount of time that can be spent on road
     * upgrades
     * 
     * @return an array of strings, each string is the name of an intersection that
     * should be upgraded
     * 
     * This method must use a dynamic programming approach to solve the problem
     */
    public String[] dynamicProgrammingSolver(int budgetLimit, int timeLimit) {
        if (upgradeData == null) {
            upgradeData = checker.upgradeAnalyser();
        }
        // Write your code below this line
        int maxMoney = budgetLimit; // the maximum weight of the knapsack
        int maxTime = timeLimit; // the maximum time of the knapsack
        int noItems = upgradeData.length; // the number of items

        int[] money = new int[upgradeData.length]; // weight of the item
        int[] time = new int[upgradeData.length]; // value of the item
        double[] cong = new double[upgradeData.length]; // value of the item

        for (int i = 0; i < upgradeData.length; i++) {
            String[] upgrade = upgradeData[i].split(", ");
            money[i] = Integer.parseInt(upgrade[1]);
            time[i] = Integer.parseInt(upgrade[2]);

            int id = intersectionStringToIndex.get(upgrade[0]);
            for (int j = 0; j < graph.adjList.get(id).size(); j++) {
                GraphEdge edge = graph.adjList.get(id).get(j);
                congestion += edge.weight;
                cong[i] += edge.weight;
            }
        }
        double[][][] f = new double[noItems + 1][maxMoney + 1][maxTime + 1]; // the value of the knapsack
        boolean[][][] p = new boolean[noItems + 1][maxMoney + 1][maxTime + 1]; // the items included in the knapsack
        
        List<Integer> ans =  knapsackSolution(noItems, maxMoney, maxTime, time, money, cong, f, p);
        //System.out.println(showResult(noItems, maxMoney, maxTime, money, time, f, p));
        List<String> result = new ArrayList<>();   
        for (int i = 0; i < ans.size(); i++) {
            result.add(upgradeData[ans.get(i)].split(", ")[0]);
        }
        return result.toArray(new String[0]);
    }

    public List<Integer> knapsackSolution(int noItems, int maxMoney, int maxTime, int[] time, int[] money, double[] cong, double[][][] f, boolean[][][] p) {
        // 3D DP table to store the maximum congestion improvement
        for (int i = 1; i <= noItems; i++) {
            for (int j = 1; j <= maxMoney; j++) {
                for (int k = 1; k <= maxTime; k++) {
                    if (money[i - 1] > j || time[i - 1] > k) {
                        f[i][j][k] = f[i - 1][j][k];
                        p[i][j][k] = false;
                    } else {
                        f[i][j][k] = Math.max(f[i - 1][j][k], f[i - 1][j - money[i - 1]][k - time[i - 1]] + cong[i - 1]);
                        p[i][j][k] = f[i - 1][j][k] < f[i - 1][j - money[i - 1]][k - time[i - 1]] + cong[i - 1];
                    }
                }
            }
        }
    
        // Prepare the result (selected intersections)
        String result = "";
        List<Integer> selectedNodes = new ArrayList<>();
        int l = maxMoney;
        int t = maxTime;
        for (int k = noItems; k >= 1; k--) {
            result += "item " + (k-1) + " Included: " +p[k][l][t] +"\n";
            if (money[k - 1] <= l && time[k - 1] <= t && p[k][l][t]) {
                if (p[k][l][t] || !graph.nodeList.get(intersectionStringToIndex.get(intersectionIndexToString.get(k - 1))).selected) {
                    graph.selectNode(intersectionStringToIndex.get(intersectionIndexToString.get(k - 1)));
                    l = l - money[k - 1];
                    t = t - time[k - 1];
                }
            }
        }
        String[] output = result.split("\n");
        for (int k = 0; k < output.length; k++) {
            if (output[k].contains("true")) {
                selectedNodes.add(Integer.parseInt(output[k].split(" ")[1]));
            }
        }
        return selectedNodes;
    }

    /*
     * @param budgetLimit the maximum amount of money that can be spent on road
     * upgrades
     * 
     * @param timeLimit the maximum amount of time that can be spent on road
     * upgrades
     * 
     * @return an array of strings, each string is the name of an intersection that
     * should be upgraded
     * 
     * This method must use a heuristic algorithm to solve the problem
     */
    public String[] heuristicSolver(int budgetLimit, int timeLimit) {
        if (upgradeData == null) {
            upgradeData = checker.upgradeAnalyser();
        }
        // Write your code below this line

        throw new UnsupportedOperationException("Not implemented yet."); // Remove this line when you implement this
                                                                         // method
    }

    /*
     * @param budgetLimit the maximum amount of money that can be spent on road
     * upgrades
     * 
     * @param timeLimit the maximum amount of time that can be spent on road
     * upgrades
     * 
     * @return an array of strings, each string is the name of an intersection that
     * should be upgraded
     * 
     * This method must use a metaheuristic hill climbing algorithm to solve the
     * problem
     */
    public String[] hillClimbingSolver(int budgetLimit, int timeLimit) {
        if (upgradeData == null) {
            upgradeData = checker.upgradeAnalyser();
        }
        // Write your code below this line

        throw new UnsupportedOperationException("Not implemented yet."); // Remove this line when you implement this
                                                                         // method
    }
}