/*
 * COMP2230 - Algorithms
 * Assignment 2 - Answers
 * @author  Studious Student - c1234567 (Replace with your name and student number)
 * @version 1.0
 * 
 * This is where you will write your code for the assignment. The required methods have been started for you, you may add additional helper methods and classes as required.
 */

 public class UpgradeCalculator {
    private final MapGenerator mapGen;
    public String [] upgradeData;
    public UpgradeCheck checker;
    public String cityMap = null;
    
    public UpgradeCalculator(int seed){
        mapGen = new MapGenerator(seed); // Pass a seed to the random number generator, allows for reproducibility
    }

    /*
     * This method must load the city map from the map generator and store it
     * You may assume that all marking scripts will call this method before any others.
     * Do not modify the code above the comment line in this method - this will be used to manually insert specific maps for marking purposes.
     * Write your code below the comment line where indicated.
     */
    public void loadMap(){
        if (cityMap == null) {
            cityMap = mapGen.generateMap();
        }

        // Write your code below this line - just copy from your assignment 1 solution, it's the same map generator
        
        throw new UnsupportedOperationException("Not implemented yet."); // Remove this line when you implement this method
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

        throw new UnsupportedOperationException("Not implemented yet."); // Remove this line when you implement this method
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

        throw new UnsupportedOperationException("Not implemented yet."); // Remove this line when you implement this method
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

        throw new UnsupportedOperationException("Not implemented yet."); // Remove this line when you implement this method
    }
}