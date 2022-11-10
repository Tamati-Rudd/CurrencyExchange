package currencyexchangeproblem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class:
 * - Runs Floyd-Warshall on a n x n table of weights
 * - Checks diagonals of the distance matrix for arbitrage (negative values)
 * - When arbitrage is found, navigates the path to find the negative cycle (location)
 * - Prints the results
 * Path reconstruction:
 * https://gopalcdas.com/2018/02/04/solution-currency-arbitrage/
 * https://gopalcdas.com/2018/01/20/floyd-warshall-algorithm/
 * @author Tamati Rudd 18045626
 */
public class ArbitrageFinder extends AllPairsFloydWarshall {
    public Map<Currencies, Double> arbitrageValues;
    public Map<Currencies, String> arbitragePaths;

    /**
     * Construct a new arbitrage finder
     * @param weights table of weights for Floyd-Warshall
     */
    public ArbitrageFinder(double[][] weights) {
        super(weights);
        arbitrageValues = new TreeMap<>();
        arbitragePaths = new HashMap<>();
    }

    /**
     * Build a string representation of matrices d[n] and p[n] Checks the
     * diagonals of the distance (shortest paths) matrix for negative values, as
     * this indicates negative cycles - arbitrageValues
     *
     * @return string representations of the matrices
     */
    public String toString() {
        String output = "Shortest path weights\n";

        //Add headings above distance matrix
        for (int i = 0; i < n; i++) {
            output += "\t" + Currencies.values()[i] + "\t\t";
        }
        output += "\n";

        //Build distance (length/paths) matrix
        for (int i = 0; i < n; i++) {
            output += (Currencies.values()[i]);
            for (int j = 0; j < n; j++) {
                if (d[n][i][j] != INFINITY) {
                    output += ("\t" + d[n][i][j]);

                    //If a diagonal in the matrix, check for arbitrage
                    if (i == j) {
                        checkForArbitrage(i, j);
                    }
                } else {
                    output += "\tinfin";
                }
            }
            output += "\n";
        }

        //Build r vertices matrix
        output += "Previous vertices on shortest paths\n";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (p[n][i][j] != NO_VERTEX) {
                    output += ("\t" + Currencies.values()[p[n][i][j]]);
                } else {
                    output += "\tnull";
                }
            }
            output += "\n";
        }
        return output;
    }

    /**
     * Check a shortest path distance on a matrix diagonal for arbitrage (negative value)
     * @param i matrix row
     * @param j matrix column
     */
    protected void checkForArbitrage(int i, int j) {
        //Check for negative value 
        if (d[n][i][j] < 0) {
            arbitrageValues.put(Currencies.values()[i], d[n][i][j]);
            getArbitragePath(i, j);
        }
    }
    
    /**
     * If arbitrage is found, determine its location by navigating the path from vertex i to j
     * @param i matrix row / origin vertex
     * @param j matrix column / destination vertex
     * Note: i and j will always be equal as this is called on finding a negative cycle
     */
    protected void getArbitragePath(int i, int j) {
        boolean findingPath = true;
        ArrayList<Integer> visited = new ArrayList<>();
        String path = Currencies.values()[i]+" -> "; //print i
        
        //Get and record the previous vertex from the initial currency
        int previous = (int) p[n][i][j];
        path += Currencies.values()[previous];
        visited.add(previous);

        //Navigate the path until the negative cyle is found
        //r = next vertex in the path
        if (previous != j) {
            //Setup vertex r for the first iteration
            int r = (int) p[n][i][previous];
            while (r != j && findingPath) {
                //Add to path
                path += " -> ";
                path += Currencies.values()[r];
                
                //Check if vertex r has already been visited
                if (!visited.contains(r)) { //Visit r and begin new iteration
                    visited.add(r);
                    r = (int) p[n][i][r];
                } else { //Negative cycle has been found
                    path += " ... (infinite loop)";
                    findingPath = false;
                }
            }
        }
        
        arbitragePaths.put(Currencies.values()[i], path);
    }
}
