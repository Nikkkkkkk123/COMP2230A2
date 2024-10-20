/*
 * COMP2230 - Algorithms
 * Assignment 2 - Answers
 * @author  Nikkita Nichols - c3362623
 * @version 1.0
 * 
 * This is where you will write your code for the assignment. The required methods have been started for you, you may add additional helper methods and classes as required.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Random;

class GraphNode {
    // the node id
    public int Id;

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

    public UpgradeCalculator(int seed) {
        mapGen = new MapGenerator(seed); // Pass a seed to the random number generator, allows for reproducibility
        checker = new UpgradeCheck(seed);
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
            cityMap = mapGen.generateMap(); // You can optionally pass in an integer here to set the size of the largest
                                            // connected component in the map
        }

        // Write your code below this line - just copy from your assignment 1 solution,
        // it's the same map generator

        // Reset data structures to allow for multiple calls to loadMap. This avoids
        // data from previous calls to loadMap being used in the current call.
        roadStringToIndex.clear();
        roadIndexToString.clear();
        intersectionStringToIndex.clear();
        intersectionIndexToString.clear();
        endPointToRoadName.clear();
        roadMap.clear();
        graph = null;

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

        // Get all required informaiton. including the congestion gained for the node
        int[] money = new int[upgradeData.length]; // weight of the item
        int[] time = new int[upgradeData.length]; // value of the item
        int noItems = upgradeData.length;

        // Get the money and time for each upgrade
        for (int i = 0; i < noItems; i++) {
            String[] upgrade = upgradeData[i].split(", ");
            money[i] = Integer.parseInt(upgrade[1]);
            time[i] = Integer.parseInt(upgrade[2]);
        }

        double[][][] total = new double[noItems + 1][budgetLimit + 1][timeLimit + 1];
        boolean[][][] selected = new boolean[noItems + 1][budgetLimit + 1][timeLimit + 1];

        // Perform the knapsack algorithm
        performKnapsack(noItems, budgetLimit, timeLimit, money, time, total, selected);

        List<String> result = new ArrayList<>(); // Used to store the result
        int i = noItems;
        int j = budgetLimit;
        int k = timeLimit;

        // Run through all items from the last item to the first item and add the
        // selected items to the result if they
        // were selected in the knapsack algorithm
        while (i > 0) {

            // Check if the item was selected in the knapsack algorithm. If it was selected,
            // add the item to the result
            if (selected[i][j][k]) {
                result.add(intersectionIndexToString
                        .get(intersectionStringToIndex.get(upgradeData[i - 1].split(", ")[0])));
                j -= money[i - 1];
                k -= time[i - 1];
            }
            i--;
        }
        return result.toArray(new String[result.size()]); // Converts the result to an array and returns it
    }

    /*
     * Method: performKnapsack
     * Description: This method performs the knapsack algorithm to determine the
     * optimal road upgrades to select
     *
     * @param noItems the number of road upgrades available
     * 
     * @param budgetLimit the maximum amount of money that can be spent on road
     * upgrades
     * 
     * @param timeLimit the maximum amount of time that can be spent on road
     * upgrades
     * 
     * @param money an array of the cost of each road upgrade
     * 
     * @param time an array of the time taken for each road upgrade
     * 
     * @param total a 3D array to store the total value of the selected road
     * upgrades
     * 
     * @param selected a 3D array to store whether each road upgrade is selected
     *
     * @return void
     */
    private void performKnapsack(int noItems, int budgetLimit, int timeLimit, int[] money, int[] time,
            double[][][] total, boolean[][][] selected) {

        // Perform base case for the knapsack algorithm when 0 items are selected
        for (int j = 0; j <= budgetLimit; j++) {
            for (int k = 0; k <= timeLimit; k++) {
                total[0][j][k] = 0;
                selected[0][j][k] = false;
            }
        }

        // Perform the knapsack algorithm for all items
        for (int i = 1; i <= noItems; i++) {
            for (int m = 1; m <= budgetLimit; m++) {
                for (int n = 1; n <= timeLimit; n++) {

                    // If the upgrade is too expensive or takes too long, do not select it
                    if (money[i - 1] > m || time[i - 1] > n) {
                        total[i][m][n] = total[i - 1][m][n];
                        selected[i][m][n] = false;
                    }

                    // If the upgrade can be selected, select it
                    else {
                        HashSet<Integer> selectedNodes = new HashSet<>();
                        double value = 0;
                        int usedMoney = m;
                        int usedTime = n;
                        // Run through all previous items and check if they can be selected
                        for (int j = i; j >= 1; j--) {
                            // Check if the budget and time constraints can afford the upgrade
                            if (usedMoney - money[j - 1] >= 0 && usedTime - time[j - 1] >= 0) {
                                if (selected[j][usedMoney][usedTime] || i == j) {
                                    // Run through all the edges of the selected node and add the congestion value
                                    // of the node to the total value
                                    for (GraphEdge edge : graph.adjList
                                            .get(intersectionStringToIndex.get(upgradeData[j - 1].split(", ")[0]))) {
                                        // Check if the target node has already been selected. If it has not been
                                        // selected, add the congestion value of the node to the total value
                                        if (!selectedNodes.contains(edge.target)) {
                                            value += edge.weight;
                                        }
                                        // Check if the source node has already been selected. If it has not been
                                        // selected, add the congestion value of the node to the total value
                                        if (!selectedNodes.contains(edge.source)) {
                                            usedMoney -= money[j - 1];
                                            usedTime -= time[j - 1];
                                            selectedNodes.add(edge.source);
                                        }
                                    }
                                }
                            }
                        }

                        // Check if the value of the selected upgrade is greater than the value of the
                        // previous upgrade. if it is, select the upgrade. Otherwise, do not select the
                        // upgrade
                        if (value > total[i - 1][m][n]) {
                            total[i][m][n] = value;
                            selected[i][m][n] = true;
                        } else {
                            total[i][m][n] = total[i - 1][m][n];
                            selected[i][m][n] = false;
                        }
                    }
                }
            }
        }
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
        // Get all required informaiton. including the congestion gained for the node
        int maxMoney = budgetLimit; // the maximum weight of the knapsack
        int maxTime = timeLimit; // the maximum time of the knapsack

        int[] money = new int[upgradeData.length]; // weight of the item
        int[] time = new int[upgradeData.length]; // value of the item
        double[] cong = new double[upgradeData.length]; // value of the item
        ArrayList<NodeValue> nodeValues = new ArrayList<>(); // value of the item

        // Get the money and time for each upgrade
        for (int i = 0; i < upgradeData.length; i++) {
            String[] upgrade = upgradeData[i].split(", ");
            money[i] = Integer.parseInt(upgrade[1]);
            time[i] = Integer.parseInt(upgrade[2]);

            // Get the id of the intersection in the graph
            // This allows for the correct endpoint to be selected
            // As the data in upgradeData is not necessarily in the same order as the graph
            int id = intersectionStringToIndex.get(upgrade[0]);

            // Get the congestion value of the node
            for (int j = 0; j < graph.adjList.get(id).size(); j++) {
                GraphEdge edge = graph.adjList.get(id).get(j);
                cong[i] += edge.weight;
            }

            // If the upgrade is affordable and within the time limit, add it to the list of
            // node values
            if (money[i] <= maxMoney && time[i] <= maxTime) {
                double itemValue = cong[i] / (money[i] + time[i]); // Calculate the value of the node
                nodeValues.add(new NodeValue(i, itemValue));
            }
        }

        // Sort the node values in descending order allowing for the most valuable nodes
        // to be selected first
        nodeValues.sort((a, b) -> Double.compare(b.value, a.value));

        List<String> result = new ArrayList<>();
        List<String> selectedNodes = new ArrayList<>();
        int l = maxMoney;
        int t = maxTime;

        // Loop through all the nodes and select the nodes that are affordable and
        // within the time limit
        // The max money left and max time left are updated after each node is selected
        for (NodeValue node : nodeValues) {
            if (money[node.nodeID] > l || time[node.nodeID] > t) {
                continue; // skip this node
            }

            // If the node is affordable and within the time limit, add it to the selected
            // nodes
            else {
                result.add(upgradeData[node.nodeID].split(", ")[0]);
                l -= money[node.nodeID]; // update the max money left
                t -= time[node.nodeID]; // update the max time left
            }
        }

        return result.toArray(new String[result.size()]); // Converts the result to an array and returns it
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
        Random rng = new Random(); // Initialize a random number generator
        int bigM = 9999;
        int noItems = upgradeData.length; // Get the number of items available
        int[] money = new int[noItems];
        int[] time = new int[noItems];

        // Get the money and time for each upgrade
        for (int i = 0; i < noItems; i++) {
            String[] upgrade = upgradeData[i].split(", ");
            money[i] = Integer.parseInt(upgrade[1]);
            time[i] = Integer.parseInt(upgrade[2]);
        }

        // Make an initial solution and evaluate it
        boolean[] solution = initializeSolution(noItems, rng);
        double[] score = evalSolution(bigM, solution, noItems, money, time, budgetLimit, timeLimit);

        // Save the first solution as the best solution
        boolean[] bestSolution = solution.clone();
        double[] bestScore = score.clone();

        // Perform the hill climbing algorithm
        for (int i = 0; i < 1000; i++) {

            // generate a flip move randomly
            int flipIndex = rng.nextInt(noItems);

            // Get score of the new solution
            double[] newScore = evalFlippedSolution(bigM, solution, score, noItems, money, time, budgetLimit, timeLimit,
                    flipIndex);

            // Check if the new score is better than the current score, if it is, apply the
            // flip move to the solution
            if (newScore[0] < score[0]) {

                // Apply the flip move to the solution
                applyFlip(solution, score, flipIndex, money, time, bigM, budgetLimit, timeLimit, noItems);

                // Check if the new score is better than the best score, if it is, update the
                // best solution and best score
                if (score[0] < bestScore[0]) {
                    bestSolution = solution.clone();
                    bestScore = score.clone();
                }
            }
        }

        // Convert the best solution to an array of strings and return the result
        List<String> result = new ArrayList<>();

        // Loop through all items and add the selected items to the result
        for (int i = 0; i < noItems; i++) {
            if (bestSolution[i]) {
                result.add(upgradeData[i].split(", ")[0]);
            }
        }

        return result.toArray(new String[result.size()]); // Converts the result to an array and returns it
    }

    /*
     * Method: initializeSolution
     * Description: This method initializes a solution for the hill climbing
     * algorithm
     *
     * @param noItems the number of road upgrades available
     * 
     * @param rng the random number generator
     *
     * @return a boolean array representing the solution
     */
    private boolean[] initializeSolution(int noItems, Random rng) {
        boolean[] solution = new boolean[noItems];
        // loops through all items and randomly selects whether the upgrade is selected
        // or not
        for (int i = 0; i < noItems; i++) {
            solution[i] = rng.nextBoolean(); // Randomly select whether the upgrade is selected or not
        }
        return solution; // Returns the solution
    }

    /*
     * Method: evalSolution
     * Description: This method evaluates the solution for the hill climbing
     * algorithm
     *
     * @param bigM the value of bigM
     * 
     * @param solution the solution to evaluate
     * 
     * @param noItems the number of road upgrades available
     * 
     * @param money an array of the cost of each road upgrade
     * 
     * @param time an array of the time taken for each road upgrade
     * 
     * @param budgetLimit the maximum amount of money that can be spent on road
     * upgrades
     * 
     * @param timeLimit the maximum amount of time that can be spent on road
     * upgrades
     *
     * @return a double array representing the score of the solution
     */
    private double[] evalSolution(int bigM, boolean[] solution, int noItems, int[] money, int[] time, int budgetLimit,
            int timeLimit) {
        double[] score = new double[4]; // Initialize an array to store the score of the solution. The score is stored
                                        // in the following order: total score, total money, total time, total
                                        // congestion
        HashSet<Integer> selectedNodes = new HashSet<>(); // Initialize a hashset to store the selected nodes

        // Loop through all items and calculate the total money, total time, and total
        // congestion of the selected upgrades
        for (int i = 0; i < noItems; i++) {

            // Check if the upgrade is selected
            if (solution[i]) {

                // Add the money, time, and congestion of the upgrade to the total money, total
                // time, and total congestion
                score[1] += money[i];
                score[2] += time[i];

                // Loop through all edges of the upgrade and add the congestion value of the
                // node to the total congestion
                for (GraphEdge edge : graph.adjList.get(intersectionStringToIndex.get(upgradeData[i].split(", ")[0]))) {

                    // Only add the congestion value of the node if the node has not been selected
                    if (!selectedNodes.contains(edge.target)) {
                        score[3] += edge.weight;
                    }
                }

                selectedNodes.add(intersectionStringToIndex.get(upgradeData[i].split(", ")[0])); // Add the selected
                                                                                                 // node to the selected
                                                                                                 // nodes
            }
        }

        score[0] = Math.max(0, score[1] - budgetLimit) + Math.max(0, score[2] - timeLimit) * bigM - score[3]; // Calculate
                                                                                                              // the
                                                                                                              // total
                                                                                                              // score
                                                                                                              // of the
                                                                                                              // solution

        return score;
    }

    /*
     * Method: applyFlip
     * Description: This method applies a flip move to the solution for the hill
     * climbing algorithm
     *
     * @param solution the solution to apply the flip move to
     * 
     * @param score the score of the solution
     * 
     * @param flipIndex the index of the upgrade to flip
     * 
     * @param money an array of the cost of each road upgrade
     * 
     * @param time an array of the time taken for each road upgrade
     * 
     * @param bigM the value of bigM
     * 
     * @param budgetLimit the maximum amount of money that can be spent on road
     * upgrades
     * 
     * @param timeLimit the maximum amount of time that can be spent on road
     * upgrades
     * 
     * @param noItems the number of road upgrades available
     *
     * @return void
     */
    private void applyFlip(boolean[] solution, double[] score, int flipIndex, int[] money, int[] time, int bigM,
            int budgetLimit, int timeLimit, int noItems) {
        HashSet<Integer> selectedNodes = new HashSet<>(); // Initialize a hashset to store the selected nodes

        // Loop through all items and add the selected nodes to the selected nodes
        // hashset
        for (int i = 0; i < noItems; i++) {

            // Check if the upgrade is selected. if it is selected, add the node to the
            // selected nodes hashset
            if (solution[i]) {
                selectedNodes.add(intersectionStringToIndex.get(upgradeData[i].split(", ")[0]));
            }
        }

        // Check if the upgrade is selected. If it is selected, remove the upgrade from
        // the solution and update the score
        if (solution[flipIndex]) {
            solution[flipIndex] = false; // Swap the upgrade to not selected
            score[1] -= money[flipIndex]; // Subtract the money of the upgrade from the total money
            score[2] -= time[flipIndex]; // Subtract the time of the upgrade from the total time

            // Loop through all edges of the upgrade and subtract the congestion value of
            // the node from the total congestion
            for (GraphEdge edge : graph.adjList
                    .get(intersectionStringToIndex.get(upgradeData[flipIndex].split(", ")[0]))) {

                // Only subtract the congestion value of the node if the node has not been
                // selected
                if (!selectedNodes.contains(edge.target)) {
                    score[3] -= edge.weight;
                }
            }
            score[0] = Math.max(0, score[1] - budgetLimit) + Math.max(0, score[2] - timeLimit) * bigM - score[3]; // Obtain
                                                                                                                  // the
                                                                                                                  // new
                                                                                                                  // score
                                                                                                                  // of
                                                                                                                  // the
                                                                                                                  // solution
        }

        // If the upgrade is not selected, add the upgrade to the solution and update
        // the score
        else {
            solution[flipIndex] = true; // Swap the upgrade to selected
            score[1] += money[flipIndex];
            score[2] += time[flipIndex];

            // Loop through all edges of the upgrade and add the congestion value of the
            // node to the total congestion
            for (GraphEdge edge : graph.adjList
                    .get(intersectionStringToIndex.get(upgradeData[flipIndex].split(", ")[0]))) {

                // Only add the congestion value of the node if the node has not been selected
                if (!selectedNodes.contains(edge.target)) {
                    score[3] += edge.weight;
                }
            }
            score[0] = Math.max(0, score[1] - budgetLimit) + Math.max(0, score[2] - timeLimit) * bigM - score[3]; // Obtain
                                                                                                                  // the
                                                                                                                  // new
                                                                                                                  // score
                                                                                                                  // of
                                                                                                                  // the
                                                                                                                  // solution
        }
    }

    /*
     * Method: evalFlippedSolution
     * Description: This method evaluates a flipped solution for the hill climbing
     * algorithm
     *
     * @param bigM the value of bigM
     * 
     * @param solution the solution to evaluate
     * 
     * @param score the score of the solution
     * 
     * @param noItems the number of road upgrades available
     * 
     * @param money an array of the cost of each road upgrade
     * 
     * @param time an array of the time taken for each road upgrade
     * 
     * @param budgetLimit the maximum amount of money that can be spent on road
     * upgrades
     * 
     * @param timeLimit the maximum amount of time that can be spent on road
     * upgrades
     * 
     * @param flipIndex the index of the upgrade to flip
     *
     * @return a double array representing the score of the flipped solution
     */
    private double[] evalFlippedSolution(int bigM, boolean[] solution, double[] score, int noItems, int[] money,
            int[] time, int budgetLimit, int timeLimit, int flipIndex) {

        double[] flippedScore = score.clone(); // Clone the score of the solution
        HashSet<Integer> selectedNodes = new HashSet<>(); // Initialize a hashset to store the selected nodes

        // Loop through all items and add the selected nodes to the selected nodes
        // hashset
        for (int i = 0; i < noItems; i++) {

            // Check if the upgrade is selected. if it is selected, add the node to the
            // selected nodes hashset
            if (solution[i]) {
                selectedNodes.add(intersectionStringToIndex.get(upgradeData[i].split(", ")[0]));
            }
        }

        // Check if the upgrade is selected. If it is selected, remove the upgrade from
        // the solution and update the score
        if (solution[flipIndex]) {
            flippedScore[1] -= money[flipIndex]; // Subtract the money of the upgrade from the total money
            flippedScore[2] -= time[flipIndex]; // Subtract the time of the upgrade from the total time

            // Loop through all edges of the upgrade and subtract the congestion value of
            // the node from the total congestion
            for (GraphEdge edge : graph.adjList
                    .get(intersectionStringToIndex.get(upgradeData[flipIndex].split(", ")[0]))) {

                // Only subtract the congestion value of the node if the node has not been
                // selected
                if (!selectedNodes.contains(edge.target)) {
                    flippedScore[3] -= edge.weight;
                }
            }

            flippedScore[0] = Math.max(0, flippedScore[1] - budgetLimit)
                    + Math.max(0, flippedScore[2] - timeLimit) * bigM - flippedScore[3]; // Obtain the new score of the
                                                                                         // solution
        }

        // If the upgrade is not selected, add the upgrade to the solution and update
        // the score
        else {
            flippedScore[1] += money[flipIndex]; // Add the money of the upgrade to the total money
            flippedScore[2] += time[flipIndex]; // Add the time of the upgrade to the total time

            // Loop through all edges of the upgrade and add the congestion value of the
            // node to the total congestion
            for (GraphEdge edge : graph.adjList
                    .get(intersectionStringToIndex.get(upgradeData[flipIndex].split(", ")[0]))) {

                // Only add the congestion value of the node if the node has not been selected
                if (!selectedNodes.contains(edge.target)) {
                    flippedScore[3] += edge.weight;
                }
            }

            flippedScore[0] = Math.max(0, flippedScore[1] - budgetLimit)
                    + Math.max(0, flippedScore[2] - timeLimit) * bigM - flippedScore[3]; // Obtain the new score of the
                                                                                         // solution
        }

        return flippedScore; // Return the score of the flipped solution
    }
}