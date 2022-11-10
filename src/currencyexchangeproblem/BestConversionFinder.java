package currencyexchangeproblem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class:
 * - Converts a table of exchange rates to a map of weights and edges
 * - Finds the shortest path between two currencies (passed in?) based on the weights calculated
 * https://www.youtube.com/watch?v=obWXjtg0L64&t=197s
 * @author Tamati Rudd 18045626
 */
public class BestConversionFinder<E> {
    private String[] tableCurrencyOrder;
    
    /**
     * Construct a new Best Conversion Finder
     * @param tableCurrencyOrder the order the currencies are listed in the conversion table
     */
    public BestConversionFinder(String[] tableCurrencyOrder) {
        this.tableCurrencyOrder = tableCurrencyOrder;
    }
    
    /**
     * Calculate the weights of all possible graph edge weights
     * @param exchangeRates n x n exchange rate table to use for calculations
     * @return n x n table of graph edge weights
     */
    public double[][] calculateWeights(Double[][] exchangeRates) {
        double[][] weights = new double[exchangeRates.length][exchangeRates[0].length];
        
        //For each possible weight
        for (int i = 0; i < exchangeRates.length; i++) {
            for (int j = 0; j < exchangeRates[i].length; j++) {
                //Only include a calculated weight if the conversion isn't 1 (same currency) or 0 (no exchange)
                if (exchangeRates[i][j] != 0 && exchangeRates[i][j] != 1) {
                    weights[i][j] = Math.log(1/exchangeRates[i][j]);
                } else { //Put in a filler weight
                    weights[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
        
        return weights;
    }
    
    /**
     * Build a map of graph edges to  weights
     * Also creates graph edges based on the table weights
     * @param weightsTable the table of graph edge weights from calculateWeights
     * @param graph the graph which edges are added to
     * @return the map of edges and weights 
     */
    public Map<Edge<String>, Double> buildWeightsMap(double[][] weightsTable, GraphADT graph) {
        //Setup data structures
        Map<Edge<String>, Double> weightsMap = new HashMap<>();
        ArrayList<Vertex> vertexes = new ArrayList<>();
        vertexes.addAll(graph.vertexSet());
        
        //For each possible weight
        for (int i = 0; i < weightsTable.length; i++) {
            for (int j = 0; j < weightsTable[i].length; j++) {
                //When an entry is found in the weights table (not max value filler)
                if (weightsTable[i][j] != Double.POSITIVE_INFINITY) {
                    //Determine which currencies the table weight is for
                    Currencies rowCurrency = Currencies.values()[i];
                    Currencies columnCurrency = Currencies.values()[j];

                    //Get the vertexes for the two currencies involved in an exchange
                    Iterator<Vertex> it = vertexes.iterator();
                    Vertex<Currencies> fromCurrency = null, toCurrency = null;
                    while (it.hasNext()) {
                        Vertex v = it.next();
                        if (v.getUserObject() == rowCurrency) {
                            fromCurrency = v;
                        } else if (v.getUserObject() == columnCurrency) {
                            toCurrency = v;
                        }
                    }
                    
                    //Create a new graph edge, and add the edge to the map of weights
                    Edge<String> newEdge = graph.addEdge(fromCurrency, toCurrency);
                    weightsMap.put(newEdge, weightsTable[i][j]);
                }
            }
        }
        
        return weightsMap;
    }   
    
    /**
     * Uses Bellman-Ford to find the best conversion (shortest path) from one currency to all other currencies
     * Assumes no negative weight closed path, so doesn't check for this
     * @param graph the graph to use
     * @param weights a map of edges to weights
     * @param source the starting currency
     * @return the best conversions (shortest paths)
     */
    public Map<Vertex<E>, Edge<E>> BellmanFord(GraphADT graph, Map<Edge<String>, Double> weights, Vertex source) {
        //List of vertexes and list of edges
        ArrayList<Vertex> vertexes = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>(); 
        vertexes.addAll(graph.vertexSet()); 
        edges.addAll(graph.edgeSet());
        //d (shortest paths to each vertex)
        Map<Vertex<E>, Double> shortestPaths = new HashMap<>(); 
        //leastEdge (last edge on shortest path to a vertex)
        Map<Vertex<E>,Edge<E>> leastEdges = new HashMap<Vertex<E>,Edge<E>>(); 
        
        //Setup initial state
        Iterator<Vertex> itVertex = vertexes.iterator();
        while (itVertex.hasNext()) { //For each vertex
            Vertex v = itVertex.next();
            shortestPaths.put(v, Double.MAX_VALUE);
            leastEdges.put(v, null);
        }
        shortestPaths.put(source, 0.0);

        //Perform iterations. Iterations = amount of vertexes - 1
        for (int i = 0; i < vertexes.size(); i++) {
            Iterator<Edge> itEdge = edges.iterator();
            while (itEdge.hasNext()) { //For every edge in the graph
                //Get the end verticies of an edge e = (endVerticies[0], endVerticies[1])
                Edge e = itEdge.next();
                Vertex[] endVerticies = e.endVertices();
                //Check for shorter path: weight of start of e + weight of e < weight of end of e
                if (shortestPaths.get(endVerticies[0]) + weights.get(e) < shortestPaths.get(endVerticies[1])) {
                    //Shorter path to the end of e (endVerticies[1]) found
                    shortestPaths.put(endVerticies[1], shortestPaths.get(endVerticies[0]) + weights.get(e));
                    //Update last edge on the shortest path estimate to e (endVerticies[1])
                    leastEdges.put(endVerticies[1], e);
                }
            }
        }
        
        //Check for negative weight closed path: an arbitrage
        //This is done by doing an additional iteration to see if any change is made
        //Does not return false if found
        Iterator<Edge> itEdge = edges.iterator();
        while (itEdge.hasNext()) {
            Edge e = itEdge.next();
            Vertex[] endVerticies = e.endVertices();
            if (shortestPaths.get(endVerticies[0]) + weights.get(e) < shortestPaths.get(endVerticies[1])) {
//                System.out.println("AN ARBITRAGE HAS BEEN FOUND");
            }
        }
        
        //Return map of vertexes to shortest paths
        return leastEdges;
    }
}
