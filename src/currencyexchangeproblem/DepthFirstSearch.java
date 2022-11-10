package currencyexchangeproblem;

/**
 * A class which contains the depth first search algorithm for any graph that
 * implements the GraphADT interface and whose vertices hold elements of generic
 * type E that has suitable hashing function
 * @author Andrew Ensor
 *
 * Sourced from the ADA course code files
 * Sub-classed for this assessment
 * @see BridgeExchangeFinder
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DepthFirstSearch<E> {
    // colours for each vertex where WHITE is unvisited, GREY is 
    // currently being processed, and BLACK is completely processed

    protected static enum Colour {
        WHITE, GREY, BLACK
    };
    protected Map<Vertex<E>, Colour> vertexColours;
    protected GraphADT<E> graph;

    // creates new searcher which has performed no depth-first search
    public DepthFirstSearch(GraphADT<E> graph) {
        this.graph = graph;
        Set<Vertex<E>> vertices = graph.vertexSet();
        vertexColours = new HashMap<Vertex<E>, Colour>(vertices.size());
        for (Vertex<E> vertex : vertices) {
            vertexColours.put(vertex, Colour.WHITE);
        }
    }

    // performs a recursive depth-first search of graph starting at
    // given vertex which is presumed to be WHITE
    public void search(Vertex<E> startVertex) {
        if (!graph.containsVertex(startVertex)) {
            throw new IllegalArgumentException("vertex not in graph");
        }
        // handle the starting vertex
        vertexColours.put(startVertex, Colour.GREY);
        vertexDiscovered(startVertex);
        // visit each adjacent vertex
        for (Edge<E> incidentEdge : startVertex.incidentEdges()) {
            Vertex<E> adjacentVertex = incidentEdge.oppositeVertex(startVertex);
            if (vertexColours.get(adjacentVertex) == Colour.WHITE) {
                edgeTraversed(incidentEdge);
                search(adjacentVertex);
            }
        }
        vertexColours.put(startVertex, Colour.BLACK);
        vertexFinished(startVertex);
    }

    // hook method that is called whenever a vertex has been discovered
    protected void vertexDiscovered(Vertex<E> vertex) {  // default implementation does nothing
    }

    // hook method that is called whenever a vertex has been finished
    protected void vertexFinished(Vertex<E> vertex) {  // default implementation does nothing
    }

    // hook method that is called whenever a tree edge is traversed
    protected void edgeTraversed(Edge<E> edge) {  // default implementation does nothing
    }
}
