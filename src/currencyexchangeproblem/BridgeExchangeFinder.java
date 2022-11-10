package currencyexchangeproblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * This class: 
 - Creates an undirected, unweighted graph from an n x n exchange rates table
 - Performs depth first search to collect d[v] and m[v] values
 - Uses the returned values to find bridges in the created graph
 * @author Tamati Rudd 18045626
 */
public class BridgeExchangeFinder<E> extends DepthFirstSearch<E> {
    protected static Vertex source; //Source of the exchange finder search
    protected Map<Vertex<E>, Integer> discovered; //d[v]
    protected int discoveredNum; //value of next d[v]
    protected Map<Vertex<E>, Integer> value; //m[v]
    protected ArrayList<Edge> visitedEdges;
    protected Stack parentEdges;
    protected ArrayList<Edge<E>> bridges;
    
    /**
     * Construct a BridgeExchangeFinder
     * @param graph the graph to use
     */
    public BridgeExchangeFinder(GraphADT graph) {
        super(graph);
        discovered = new HashMap<>();
        discoveredNum = 0;
        value = new HashMap<>();
        visitedEdges = new ArrayList<>();
        parentEdges = new Stack();
        bridges = new ArrayList<>();
    }
    
    /**
     * Build an undirected, unweighted graph from an n x n table of exchange rates
     * Also creates graph edges based on the table weights
     * @param exchangeRates n x n table of exchange rates
     * @return the graph
     */
    protected static GraphADT buildGraph(Double[][] exchangeRates) {
        //Setup data structures
        GraphADT newGraph = new AdjacencyListGraph(GraphADT.GraphType.UNDIRECTED);
        Map<Character, Vertex> vertexes = new HashMap<>();
        
        //Create graph vertexes, recording each one along the way
        char letter = 'a';
        for (int i = 0; i < exchangeRates.length; i++) {
            Vertex v = newGraph.addVertex(Character.toString(letter));
            vertexes.put(letter, v);
            letter++;
            
            //Record the first vertex as the source of the search
            if (i == 0) {
                source = v;
            }
        }
        
        //For each possible exchange rate (pair of currencies)
        for (int i = 0; i < exchangeRates.length; i++) {
            for (int j = 0; j < exchangeRates[i].length; j++) {
                //Check if two currencies can be exchanged
                if (exchangeRates[i][j] != 0.0) {
                    //Add a new edge
                    Character c1 = (char) (97+i);
                    Character c2 = (char) (97+j);
                    newGraph.addEdge(vertexes.get(c1), vertexes.get(c2));
                    
                    //Prevent addition of edge directed the other way (as graph is undirected)
                    exchangeRates[j][i] = 0.0; 
                }
            }
        }
        
        return newGraph;
    }   
    
    
    /**
     * When a vertex is discovered, add it to the discovered map
     * @param vertex the vertex discovered
     */
    @Override
    protected void vertexDiscovered(Vertex<E> vertex) {
        discovered.put(vertex, discoveredNum); // add vertices to list as discovered
        discoveredNum++;
    }
    
    /**
     * When a vertex is finished, calculate the value (m[v]) for that vertex
     * @param vertex the vertex that is finished (just set to BLACK)
     */
    protected void vertexFinished(Vertex<E> vertex) {  
        /*Smallest between:
            - d[v] discovered value
            - any d[v] for any adjacent GREY vertex EXCEPT the parent
            - any m[v] values of all adjacent BLACK vertexes
        */
        ArrayList<Integer> possibleValues = new ArrayList<>();
        
        //Case 1: d[v] (discovery order)
        possibleValues.add(discovered.get(vertex));
        
        //Case 2: any adjacent GREY vertex EXCEPT the vertex on the PARENT edge
        for (Edge<E> incidentEdge : vertex.incidentEdges()) {
            //Filter out the parent edge
            if (!parentEdges.isEmpty()) {
                if (parentEdges.peek() == incidentEdge) {
                    continue;
                }
            }
            //Test the colour of the adjacent (opposite) vertex
            Vertex<E> adjacentVertex = incidentEdge.oppositeVertex(vertex);
            if (vertexColours.get(adjacentVertex) == Colour.GREY) {
                //If the adjacent vertex is grey, consider its d[v] (discovery order)
                possibleValues.add(discovered.get(adjacentVertex));
            } 
        }
        
        //Case 3: any m[v] value for any adjacent BLACK vertex
        for (Edge<E> incidentEdge : vertex.incidentEdges()) {
            //Test the colour of the adjacent (opposite) vertex
            Vertex<E> adjacentVertex = incidentEdge.oppositeVertex(vertex);
            if (vertexColours.get(adjacentVertex) == Colour.BLACK) {
                //If the adjacent vertex is grey, consider its m[v] (value)
                possibleValues.add(value.get(adjacentVertex));
            } 
        }
        
        //Determine the value to use (m[vertex])
        value.put(vertex, Collections.min(possibleValues));
        //Pop off the parent edge
        if (!parentEdges.isEmpty()) {
            Object removed = parentEdges.pop();
        }
    }

    /**
     * When an edge is traversed, visit it and record it as a parent edge
     * @param edge the edge being traversed
     */
    protected void edgeTraversed(Edge<E> edge) { 
        visitedEdges.add(edge);
        parentEdges.push(edge);
    }
    
    /**
     * Find all bridges in the graph, provided that:
     *  - the d[v] table is completed 
     *  - the m[v] table is completed
     *  - all traversed edges are recorded
     */
    public void findBridges() {
        //For each visited edge
        for (Edge<E> edge : visitedEdges) {
            //Check for bridge
            Vertex<E>[] endVertices = edge.endVertices();
            if (value.get(endVertices[1]) > discovered.get(endVertices[0])) {
                bridges.add(edge);
            }
        }
    }
    
    public static void main(String[] args) {
        Double[][] exchangeRates = { //1.0 = can be exchanged. In reality, would replace with actual rates
            //a,   b,   c,   d,   e,   f,   g,   h,   i,   j,   k,   l,   m
            {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, //a
            {1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, //b
            {0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, //c
            {0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, //d
            {0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0}, //e
            {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, //f
            {0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, //g
            {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0}, //h
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0}, //i
            {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0}, //j
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0}, //k
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0}, //l
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0}, //m
        };
        
        //Build graph from n x n exchange rate table
        GraphADT<String> bridgeGraph = buildGraph(exchangeRates);
        System.out.println("Currency "+bridgeGraph);
        
        //Perform depth first search
        BridgeExchangeFinder<String> searcher = new BridgeExchangeFinder<String>(bridgeGraph);
        System.out.println("Performing depth-first search from a:");
        searcher.search(source);
        
        //Find the bridges and output results
        System.out.println("d[v] = "+searcher.discovered);
        System.out.println("m[v] = "+searcher.value);
        searcher.findBridges();
        System.out.println("Bridges: ");
        for (Edge edge : searcher.bridges) {
            System.out.println(edge);
        }
    }
}
