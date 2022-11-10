package currencyexchangeproblem;

/**
   A class that demonstrates the Floyd-Warshall algorithm for solving
   the all-pairs shortest paths problem in O(n^3)
   @author Andrew Ensor
   
   Sourced from the ADA course code files
   Reworked to use double values instead of int values, and sub-classed for this assignment
   @see ArbitrageFinder
*/

public class AllPairsFloydWarshall
{
   public static final double INFINITY = Double.POSITIVE_INFINITY;
   protected static final int NO_VERTEX = -1;
   protected int n; // number of vertices in the graph
   protected double[][][] d; //d[k][i][i] is weight of path from v_i to v_j
   protected int[][][] p; //p[k][i][i] is penultimate vertex in path
   
    public AllPairsFloydWarshall(double[][] weights)
   {  n = weights.length;
      d = new double[n+1][][];
      d[0] = weights;
      // create p[0]
      p = new int[n+1][][];
      p[0] = new int[n][n];
      for (int i=0; i<n; i++)
      {  for (int j=0; j<n; j++)
         {  if (weights[i][j]<INFINITY)
               p[0][i][j] = i;
            else
               p[0][i][j] = NO_VERTEX;
         }
      }
      // build d[1],...,d[n] and p[1],...,p[n] dynamically
      for (int k=1; k<=n; k++)
      {  d[k] = new double[n][n];
         p[k] = new int[n][n];
         for (int i=0; i<n; i++)
         {  for (int j=0; j<n; j++)
            {  double s;
               if (d[k-1][i][k-1]!=INFINITY&&d[k-1][k-1][j]!=INFINITY)
                  s = d[k-1][i][k-1] + d[k-1][k-1][j];
               else
                  s = INFINITY;
               if (d[k-1][i][j] <= s)
               {  d[k][i][j] = d[k-1][i][j];
                  p[k][i][j] = p[k-1][i][j];
               }
               else
               {  d[k][i][j] = s;
                  p[k][i][j] = p[k-1][k-1][j];
               }
            }
         }
      }
   }
   
   // returns a string representation of matrix d[n] and p[n]
   public String toString()
   {  String output = "Shortest lengths\n";
      for (int i=0; i<n; i++)
      {  for (int j=0; j<n; j++)
         {  if (d[n][i][j] != INFINITY)
               output += ("\t" + d[n][i][j]);
            else
               output += "\tinfin";
         }
         output += "\n";
      }
      output += "Previous vertices on shortest paths\n";
      for (int i=0; i<n; i++)
      {  for (int j=0; j<n; j++)
         {  if (p[n][i][j] != NO_VERTEX)
               output += ("\t" + p[n][i][j]);
            else
               output += "\tnull";
         }
         output += "\n";
      }
      return output;
   }

//   public static void main(String[] args)
//   {  double[][] weights = {
//         {0, 2, 15, INFINITY, INFINITY, INFINITY},
//         {INFINITY, 0, 9, 11, 5, INFINITY},
//         {INFINITY, -1, 0, 3, 6, INFINITY},
//         {INFINITY, INFINITY, INFINITY, 0, 5, 2},
//         {INFINITY, INFINITY, -2, INFINITY, 0, 7},
//         {INFINITY, INFINITY, INFINITY, 1, INFINITY, 0}};
//      AllPairsFloydWarshall apfw = new AllPairsFloydWarshall(weights);
//      System.out.println(apfw);
//   }
}
