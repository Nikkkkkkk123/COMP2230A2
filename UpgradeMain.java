/*
 * COMP2230 - Algorithms
 * Assignment 2 - Main
 * @author  Studious Student - c1234567 (Replace with your name and student number)
 * @version 1.0
 * 
 * This file runs the assignment; you may modify this for testing purposes, but for marking you must ensure that your code works with the original version of this file.
 * The marker will add their own code under the test comment indicated below.
 */

public class UpgradeMain {

    int RNG_SEED = 0; // Set this to whatever you like, this ensures that your results are reproducible across runs
    public static void main(String[] args) {
        UpgradeMain program = new UpgradeMain();
        program.run(args);
    }

    public void run(String[] args){
        UpgradeCalculator analyser = new UpgradeCalculator(RNG_SEED);
        UpgradeCheck verifier = new UpgradeCheck();
        analyser.checker = verifier;
        // You may run the program using the test map by passing "test" as an argument
        if(args.length > 0 && args[0].equals("test")){
            analyser.cityMap = TESTMAP;
            analyser.upgradeData = TESTUPGRADES;
        }
        
        analyser.loadMap();
        verifier.storeVerifyMap(analyser.cityMap);

        if(args.length > 0 && args[0].equals("test")){
            checkTestmap(analyser, verifier);
        }

        // Test your calling the methods you have implemented in UpgradeCalculator
        // Use UpgradeCheck to check your results.
        // The following additional methods are available in UpgradeCheck to help you with testing:
        // String getRandomIntersection() // Call storeVerifyMap first, will return a random intersection name
        // String getRandomRoad() // Call storeVerifyMap first, will return a random road name
        // Your test code may be placed below this line:


    }

    // The following test map is shown in the assignment specification (Figure 1), you may use this to test your code
    // WARNING - Note that the while all numbers in the test map are integers, the map generator and upgrade calculator may give you doubles
    private final String TESTMAP = 
        "{{Red Street, 0 - North Crossing, 2 - South Junction, 2}, {Yellow Street, 2 - South Junction, 6 - Western Metro, 4}, "
      + "{Green Street, 2 - South Junction, 5 - Eastern Plaza, 1}, {Blue Street, 5 - Eastern Plaza, 7 - Simple Shops, 2}, "
      + "{Orange Street, 7 - Simple Shops, 6 - Western Metro, 1}, {Purple Street, 4 - Tall Towers, 7 - Simple Shops, 3}, "
      + "{Pink Road, 1 - Interesting Intersection, 4 - Tall Towers, 3}, {Cyan Road, 0 - North Crossing, 1 - Interesting Intersection, 3}, "
      + "{Mauve Road, 2 - South Junction, 3 - Perfect Park, 2}, {Violet Road, 3 - Perfect Park, 4 - Tall Towers, 1}}";

    private final String [] TESTUPGRADES = {"0 - North Crossing, 1, 2", "1 - Interesting Intersection, 2, 3", "2 - South Junction, 3, 1", "3 - Perfect Park, 1, 4", "4 - Tall Towers, 1, 2", "5 - Eastern Plaza, 2, 2", "6 - Western Metro, 3, 2", "7 - Simple Shops, 2, 1"};



    // The following method is used to check the test map. It also illustrates the methods available in UpgradeCheck
    private void checkTestmap(UpgradeCalculator analyser, UpgradeCheck verifier) {

        String [] answer = analyser.dynamicProgrammingSolver(10, 10);
        String [] solution = {"0 - North Crossing", "2 - South Junction", "4 - Tall Towers", "7 - Simple Shops"}; // This is the correct solution for the test map and test upgrades given above, note that the order of the intersections is not important
        java.util.Arrays.sort(answer);
        java.util.Arrays.sort(solution);
        System.out.print("CHECK: Dynamic Programming [10/10]\n");
        if (java.util.Arrays.equals(answer, solution))
        {
            System.out.print("-> Passed\n");
        } else {
            System.out.print("-> Failed\n");
        }

        // Basic sanity check available in UpgradeCheck that you can use:
        verifier.verify_validSolution(10, 10, analyser.upgradeData, answer);

        answer = analyser.heuristicSolver(1, 100);
        solution = new String [] {"4 - Tall Towers"};
        java.util.Arrays.sort(answer);
        java.util.Arrays.sort(solution);
        System.out.print("CHECK: Heuristic [1/100]\n");
        if (java.util.Arrays.equals(answer, solution))
        {
            System.out.print("-> Passed\n");
        } else {
            System.out.print("-> Failed\n");
        }

        answer = analyser.hillClimbingSolver(100, 1);
        solution = new String [] {"2 - South Junction"};
        java.util.Arrays.sort(answer);
        java.util.Arrays.sort(solution);
        System.out.print("CHECK: Hill Climbing [1/100]\n");
        if (java.util.Arrays.equals(answer, solution))
        {
            System.out.print("-> Passed\n");
        } else {
            System.out.print("-> Failed\n");
        }
    }
}
