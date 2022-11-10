package currencyexchangeproblem;

import java.util.ArrayList;
import java.util.Map;

/**
 * Testing class for the currency exchange problem (BestConversionFinder & ArbitrageFinder)
 */
public class CurrencyExchangeProblem {

    public static void main(String[] args) {
        GraphADT<Currencies> graph = new AdjacencyListGraph<>();
        //Create vertexes (currencies)
        Vertex<Currencies> NZD = graph.addVertex(Currencies.NZD);
        Vertex<Currencies> AUD = graph.addVertex(Currencies.AUD);
        Vertex<Currencies> USD = graph.addVertex(Currencies.USD);
        Vertex<Currencies> EUR = graph.addVertex(Currencies.EUR);
        Vertex<Currencies> MXN = graph.addVertex(Currencies.MXN);
        
        String[] tableCurrencyOrder = {"AUD", "EUR", "MXN", "NZD", "USD"};
        Double[][] exchangeRates = {
            {1.0, 0.61, 0.0, 1.08, 0.72},
            {1.64, 1.0, 0.0, 1.77, 1.18},
            {0.0, 0.0, 1.0, 0.0, 0.047},
            {0.92, 0.56, 0.0, 1.0, 0.67},
            {1.39, 0.85, 21.19, 1.5, 1.0}
        };
        //Exchange rate to weight = ln(1 / rate) e.g. ln(1 / 0.72) = 0.328504067
        //Weight to exchange rate = 1 / e^weight e.g. 1 / e^0.328504067 = 0.72
        Vertex source = AUD; //Change for testing

        //Best conversion finder testing
        BestConversionFinder conversion = new BestConversionFinder(tableCurrencyOrder);
        double[][] weights = conversion.calculateWeights(exchangeRates);
        Map<Edge<String>, Double> weightsMap = conversion.buildWeightsMap(weights, graph);
        Map<Vertex<Currencies>, Edge<String>> bestConversions = conversion.BellmanFord(graph, weightsMap, source);

//        for (int i = 0; i < weights.length; i++) {
//            for (int j = 0; j < weights[i].length; j++) {
//                System.out.print("["+weights[i][j]+"]");
//            }
//            System.out.println();
//        }
      System.out.println(graph);
      System.out.println(weightsMap);

        //Print shortest paths from source to destination (key)
        System.out.println("Best Conversion Finder: \nShortest paths from " + source + ": ");
        bestConversions.forEach((key, value) -> {
            System.out.print(source + " to " + key + ": ");
            String pathString = source + " -> ";
            Vertex v = source;
            boolean findingPath = true;
            ArrayList<Vertex> visited = new ArrayList<>();

            //Navigate the shortest path from the source
            while (findingPath) {
                //Get the next adjacent vertex by checking the next edge in the shortest path
                Edge e = bestConversions.get(v);
                Vertex v2 = getAdjacent(e.endVertices(), v);
                pathString = pathString.concat(v2.toString());

                //Check whether the next adjacent vertex has been visited already
                if (!visited.contains(v2)) {  //Visit the next adjacent vertex
                    visited.add(v2);
                    v = v2;
                    if (v.equals(key)) { //If the path is finished
                        findingPath = false;
                    } else { //Else the path continues
                        pathString = pathString.concat(" -> ");
                    }
                } else { //Visited a vertex twice: arbitrage has been found, so no shortest path
                    System.out.print("Arbitrage found - no shortest path exists. ");
                    System.out.print("The path that lead to arbitrage was: ");
                    findingPath = false;
                }
            }
            System.out.print(pathString + "\n");
        });
        
        System.out.println("\nArbitrage Finder \nFloyd Warshall results:");
        ArbitrageFinder af = new ArbitrageFinder(weights);
        System.out.println(af.toString());
        System.out.println("Arbitrage Found: ");
        for (Map.Entry<Currencies, Double> entry : af.arbitrageValues.entrySet()) {
            System.out.println(entry.getKey()+" to "+entry.getKey()+" with value: "+entry.getValue()+" with path ("+af.arbitragePaths.get(entry.getKey())+")");
        }
    }

    /**
     * Helper method to get the adjacent vertex given an array of two vertexes
     * linked by an edge
     *
     * @param endVertices the vertices that are linked by an edge
     * @param original the original vertex
     * @return the adjacent vertex
     */
    public static Vertex getAdjacent(Vertex[] endVertices, Vertex original) {
        if (endVertices[0].equals(original)) {
            return endVertices[1];
        } else {
            return endVertices[0];
        }
    }
}
