/*
 * COMP2230 - Algorithms
 * Assignment 2 - Answers
 * @author  Studious Student - c1234567 (Replace with your name and student number)
 * @version 1.0
 * 
 * This is where you will write your code for the assignment. The required methods have been started for you, you may add additional helper methods and classes as required.
 */

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
    import java.util.Set;
    import java.util.HashSet;
    import java.util.Random;
 
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
     
     public UpgradeCalculator(int seed){
         mapGen = new MapGenerator(seed); // Pass a seed to the random number generator, allows for reproducibility
         checker = new UpgradeCheck(seed);
     }
 
     /*
      * This method must load the city map from the map generator and store it
      * You may assume that all marking scripts will call this method before any others.
      * Do not modify the code above the comment line in this method - this will be used to manually insert specific maps for marking purposes.
      * Write your code below the comment line where indicated.
      */
     public void loadMap(){
         if (cityMap == null) {
             cityMap = mapGen.generateMap(); // You can optionally pass in an integer here to set the size of the largest connected component in the map
         }
 
         // Write your code below this line - just copy from your assignment 1 solution, it's the same map generator

         // Reset data structures to allow for multiple calls to loadMap. This avoids data from previous calls to loadMap being used in the current call.
            roadStringToIndex.clear();
            roadIndexToString.clear();
            intersectionStringToIndex.clear();
            intersectionIndexToString.clear();
            endPointToRoadName.clear();
            roadMap.clear();

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
      * @param budgetLimit the maximum amount of money that can be spent on road upgrades
      * @param timeLimit the maximum amount of time that can be spent on road upgrades
      * 
      * @return an array of strings, each string is the name of an intersection that should be upgraded
      * 
      * This method must use a dynamic programming approach to solve the problem
      */
     public String [] dynamicProgrammingSolver(int budgetLimit, int timeLimit){
         if (upgradeData == null) {
             upgradeData = checker.upgradeAnalyser();
         }
         // Write your code below this line

         // get all required informaiton. including the congestion gained for the node

         int[] money = new int[upgradeData.length]; // weight of the item
         int[] time = new int[upgradeData.length]; // value of the item
         int noItems = upgradeData.length;
         double[] congestion = new double[upgradeData.length]; // value of the item
         double[] totalCongestion = new double[upgradeData.length]; // value of the item

         Set<String> selectedNodes = new HashSet<>();
         for (int i = 0; i < noItems; i++) {
             String[] upgrade = upgradeData[i].split(", ");
             money[i] = Integer.parseInt(upgrade[1]);
             time[i] = Integer.parseInt(upgrade[2]);
             selectedNodes.add(upgrade[0]);

             for (int j = 0; j < graph.adjList.get(intersectionStringToIndex.get(upgrade[0])).size(); j++) {
                 GraphEdge edge = graph.adjList.get(intersectionStringToIndex.get(upgrade[0])).get(j);
                 if (!selectedNodes.contains(intersectionIndexToString.get(edge.target))) {
                     congestion[i] += edge.weight;
                 }
                 totalCongestion[i] += edge.weight;
             }
         }

         double[][][] total = new double[noItems + 1][budgetLimit + 1][timeLimit + 1];
         boolean[][][] selected = new boolean[noItems + 1][budgetLimit + 1][timeLimit + 1];

         // Perform the knapsack algorithm 

            for (int i = 0; i <= noItems; i++) {
                for (int j = 0; j <= budgetLimit; j++) {
                    for (int k = 0; k <= timeLimit; k++) {
                        if (i == 0 || j == 0 || k == 0) {
                            total[i][j][k] = 0;
                        } else if (money[i - 1] <= j && time[i - 1] <= k) {
                            double value1 = total[i - 1][j][k];
                            double value2 = total[i - 1][j - money[i - 1]][k - time[i - 1]] + congestion[i - 1];
                            if (value1 > value2) {
                                total[i][j][k] = value1;
                                selected[i][j][k] = false;
                            } else {
                                total[i][j][k] = value2;
                                selected[i][j][k] = true;
                            }
                        } else {
                            total[i][j][k] = total[i - 1][j][k] + congestion[i - 1];
                    }
                }
            }}

         //throw new UnsupportedOperationException("Not implemented yet");
            List<String> result = new ArrayList<>();
            int i = noItems;
            int j = budgetLimit;
            int k = timeLimit;
            while (i > 0) {
                if (selected[i][j][k]) {
                    result.add(upgradeData[i - 1].split(", ")[0]);
                    j -= money[i - 1];
                    k -= time[i - 1];
                }
                i--;
            }
            return result.toArray(new String[result.size()]);
     }

     

 
     /*
      * @param budgetLimit the maximum amount of money that can be spent on road upgrades
      * @param timeLimit the maximum amount of time that can be spent on road upgrades
      * 
      * @return an array of strings, each string is the name of an intersection that should be upgraded
      * 
      * This method must use a heuristic algorithm to solve the problem
      */
     public String [] heuristicSolver(int budgetLimit, int timeLimit){
         if (upgradeData == null) {
             upgradeData = checker.upgradeAnalyser();
         }
         // Write your code below this line
         int maxMoney = budgetLimit; // the maximum weight of the knapsack
         int maxTime = timeLimit; // the maximum time of the knapsack
 
         int[] money = new int[upgradeData.length]; // weight of the item
         int[] time = new int[upgradeData.length]; // value of the item
         double[] cong = new double[upgradeData.length]; // value of the item
         ArrayList<NodeValue> nodeValues = new ArrayList<>();
 
         for (int i = 0; i < upgradeData.length; i++) {
             String[] upgrade = upgradeData[i].split(", ");
             money[i] = Integer.parseInt(upgrade[1]);
             time[i] = Integer.parseInt(upgrade[2]);
 
             int id = intersectionStringToIndex.get(upgrade[0]);
             for (int j = 0; j < graph.adjList.get(id).size(); j++) {
                 GraphEdge edge = graph.adjList.get(id).get(j);
                 cong[i] += edge.weight;
             }
             if (money[i] <= maxMoney && time[i] <= maxTime) {
                 double itemValue = cong[i] / (money[i] + time[i]);
                 nodeValues.add(new NodeValue(i, itemValue));
             }
         }
         
         nodeValues.sort((a, b) -> Double.compare(b.value, a.value));
         List<Integer> selectedNodes = new ArrayList<>();
 
         int l = maxMoney;   
         int t = maxTime;
 
         for (NodeValue node : nodeValues) {
             if (money[node.nodeID] > l || time[node.nodeID] > t) {
                 continue; // skip this node
             }
             else {
                 graph.selectNode(intersectionStringToIndex.get(upgradeData[node.nodeID].split(", ")[0]));
                 selectedNodes.add(node.nodeID);
                 l -= money[node.nodeID];
                 t -= time[node.nodeID];
             }
         }
         
         List<String> result = new ArrayList<>();   
         for (int i = 0; i < selectedNodes.size(); i++) {
             result.add(upgradeData[selectedNodes.get(i)].split(", ")[0]);
         }
 
         return result.toArray(new String[result.size()]);
     }
 
     /*
      * @param budgetLimit the maximum amount of money that can be spent on road upgrades
      * @param timeLimit the maximum amount of time that can be spent on road upgrades
      * 
      * @return an array of strings, each string is the name of an intersection that should be upgraded
      * 
      * This method must use a metaheuristic hill climbing algorithm to solve the problem
      */
     public String [] hillClimbingSolver(int budgetLimit, int timeLimit){
         if (upgradeData == null) {
             upgradeData = checker.upgradeAnalyser();
         }
         // Write your code below this line
            Random rng = new Random();
         int bigM = 9999;
         int noItems = upgradeData.length;
            int[] money = new int[noItems];
            int[] time = new int[noItems];
            double[] cong = new double[noItems];

         for (int i = 0; i < upgradeData.length; i++) {
            String[] upgrade = upgradeData[i].split(", ");
            money[i] = Integer.parseInt(upgrade[1]);
            time[i] = Integer.parseInt(upgrade[2]);
         }

         // Make an initial solution to start the hill climbing algorithm
         boolean[] solution = initializeSolution(noItems, rng);
         double[] score = evalSolution(bigM, solution, noItems, money, time, budgetLimit, timeLimit);

         // Save the initial solution and score as the best solution and score
            boolean[] bestSolution = solution.clone();
            double[] bestScore = score.clone();

         for (int i = 0; i < 1000; i++) {
            
            // generate a flip move randomly
            int flipIndex = rng.nextInt(noItems);

            // Get score of the new solution
            double[] newScore = evalFlippedSolution(bigM, solution, score, noItems, money, time, budgetLimit, timeLimit, flipIndex);
            
            if (newScore[0] < score[0]) {

                // Apply the flip move
                applyFlip(solution, score, flipIndex, money, time, bigM, budgetLimit, timeLimit);

                if (score[0] < bestScore[0]) {
                    bestSolution = solution.clone();
                    bestScore = score.clone();
                }
            }
         }

            List<String> result = new ArrayList<>();
            for (int i = 0; i < noItems; i++) {
                if (bestSolution[i]) {
                    result.add(upgradeData[i].split(", ")[0]);
                }
            }

         return result.toArray(new String[result.size()]);
     }

     private boolean[] initializeSolution(int noItems, Random rng) {
        boolean[] solution = new boolean[noItems];
        for (int i = 0; i < noItems; i++) {
            solution[i] = rng.nextBoolean();
        }
        return solution;
     }

     private double[] evalSolution (int bigM, boolean[] solution, int noItems, int[] money, int[] time, int budgetLimit, int timeLimit) {
        double[] score = new double[4];
        HashSet<Integer> selectedNodes = new HashSet<>();
        for (int i = 0; i < noItems; i++) {
            if (solution[i]) {
                score[1] += money[i];
                score[2] += time[i];
                
                for (GraphEdge edge : graph.adjList.get(intersectionStringToIndex.get(upgradeData[i].split(", ")[0]))) {
                    if (!selectedNodes.contains(edge.target)) {
                        score[3] += edge.weight;
                    }
                }
                selectedNodes.add(intersectionStringToIndex.get(upgradeData[i].split(", ")[0]));
            }
        }

        score[0] = Math.max(0, score[1] - budgetLimit) + Math.max(0, score[2] - timeLimit) * bigM - score[3];
        return score;
     }

     private void applyFlip(boolean[] solution, double[] score, int flipIndex, int[] money, int[] time, int bigM, int budgetLimit, int timeLimit) {
        HashSet<Integer> selectedNodes = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            if (solution[i]) {
                selectedNodes.add(intersectionStringToIndex.get(upgradeData[i].split(", ")[0]));
            }
        }
        if (solution[flipIndex]) {
            solution[flipIndex] = false;
            score[1] -= money[flipIndex];
            score[2] -= time[flipIndex];
            for (GraphEdge edge : graph.adjList.get(intersectionStringToIndex.get(upgradeData[flipIndex].split(", ")[0]))) {
                if (!selectedNodes.contains(edge.target)) {
                    score[3] -= edge.weight;
                }
            }
            score[0] = Math.max(0, score[1] - budgetLimit) + Math.max(0, score[2] - timeLimit) * bigM - score[3];
        } else {
            solution[flipIndex] = true;
            score[1] += money[flipIndex];
            score[2] += time[flipIndex];
            for (GraphEdge edge : graph.adjList.get(intersectionStringToIndex.get(upgradeData[flipIndex].split(", ")[0]))) {
                if (!selectedNodes.contains(edge.target)) {
                    score[3] += edge.weight;
                }
            }
            score[0] = Math.max(0, score[1] - budgetLimit) + Math.max(0, score[2] - timeLimit) * bigM - score[3];
        }
     }

     private double[] evalFlippedSolution (int bigM, boolean[] solution, double[] score, int noItems, int[] money, int[] time, int budgetLimit, int timeLimit, int flipIndex) {
        double[] flippedScore = score.clone();
        HashSet<Integer> selectedNodes = new HashSet<>();
        for (int i = 0; i < noItems; i++) {
            if (solution[i]) {
                selectedNodes.add(intersectionStringToIndex.get(upgradeData[i].split(", ")[0]));
            }
        }
        if (solution[flipIndex]) {
            flippedScore[1] -= money[flipIndex];
            flippedScore[2] -= time[flipIndex];
            for (GraphEdge edge : graph.adjList.get(intersectionStringToIndex.get(upgradeData[flipIndex].split(", ")[0]))) {
                if (!selectedNodes.contains(edge.target)) {
                    flippedScore[3] -= edge.weight;
                }
            }
            flippedScore[0] = Math.max(0, flippedScore[1] - budgetLimit) + Math.max(0, flippedScore[2] - timeLimit) * bigM - flippedScore[3];
        } else {
            flippedScore[1] += money[flipIndex];
            flippedScore[2] += time[flipIndex];
            for (GraphEdge edge : graph.adjList.get(intersectionStringToIndex.get(upgradeData[flipIndex].split(", ")[0]))) {
                if (!selectedNodes.contains(edge.target)) {
                    flippedScore[3] += edge.weight;
                }
            }
            flippedScore[0] = Math.max(0, flippedScore[1] - budgetLimit) + Math.max(0, flippedScore[2] - timeLimit) * bigM - flippedScore[3];
        }

        return flippedScore;
     }
 }