import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;

public class Main {
    static int max_edge_cost = 100;
    static long timeout = 10000000000L;
    static long[][] previousTimes = new long[2][100000];
    static boolean BruteForce = true, Dynamic = true, Greedy = true;
    static int max_N = 10000; // used to track how long to repeat Heuristic quality tests

    public static void main(String[] args) {
        long time;
        System.out.println("\tBrute Force\t\t\t\tDynamic Programming");
        System.out.println("N\tTime\tDR\tEDR\tTime\tDR\tEDR");
        for (int n = 4; BruteForce || Dynamic; n++) {
            int[][] costMatrix = GenerateRandomCostMatrix(n,max_edge_cost);
            System.out.print(n + "\t");
            if(BruteForce) {
                time = getCpuTime();
                BruteForce(costMatrix);
                time = getCpuTime() - time;
                previousTimes[0][n] = time;
                if(time > timeout) BruteForce = false;
                System.out.print(time + "\t");
                if(n > 7)
                    System.out.print(time / previousTimes[0][n/2] + "\t" + factorial(n) / factorial(n/2) + "\t");
                else System.out.print("NA\tNA\t");
            } else System.out.print("NA\tNA\tNA\t");
            if(Dynamic) {
                time = getCpuTime();
                Dynamic(costMatrix);
                time = getCpuTime() - time;
                previousTimes[1][n] = time;
                if(time > timeout) Dynamic = false;
                System.out.print(time + "\t");
                if(n > 7)
                    System.out.print(time / previousTimes[1][n/2] + "\t" + (Math.pow(n/4,2) * Math.pow(2,n/2)) + "\t");
                else System.out.print("NA\tNA\t");
            } else System.out.print("NA\tNA\tNA\t");
            System.out.println();
            max_N++;
        }

        System.out.println("\tGreedy");
        System.out.println("N\tTime\tDR\tEDR");
        long previousTime = 0;
        for(int n = 4; n < 20000; n *= 2) { // Start running out of heapspace after n = 16384
            int[][] costMatrix = GenerateRandomCostMatrix(n,max_edge_cost);
            System.out.print(n + "\t");
            time = getCpuTime();
            Greedy(costMatrix);
            time = getCpuTime() - time;
            if(time > timeout) Greedy = false;
            System.out.print(time + "\t");
            if(n > 4)
                System.out.print(time / previousTime + "\t" + Math.pow(n,2) / Math.pow(n/2,2));
            else System.out.print("NA\tNA");
            previousTime = time;
            System.out.println();
        }

        System.out.println("\tGreedy");
        System.out.println("N\tAvgSolCost\tAvgExSolCost\tAvgSQR");
        for(int n = 4; n < max_N; n++) {
            System.out.print(n + "\t");
            long greedy_cost = 0, exact_cost = 0;
            for(int i = 0; i < 100; i++) {
                int[][] costMatrix = GenerateRandomCostMatrix(n,max_edge_cost);
                greedy_cost += GetCost(costMatrix, Greedy(costMatrix));
                exact_cost += GetCost(costMatrix, Dynamic(costMatrix));
            }
            greedy_cost /= 100;
            exact_cost /= 100;
            System.out.println(greedy_cost + "\t" + exact_cost + "\t" + (double) (exact_cost / greedy_cost));
        }
    }

    public static int[][] GenerateRandomCostMatrix(int vertices, int max_edge_cost) {
        int[][] result = new int[vertices][vertices];
        Random rng = new Random();

        for(int i = 0; i < vertices; i++)
            for(int j = i; j < vertices; j++) {
                result[i][j] = i == j ? 0 : rng.nextInt(max_edge_cost);
                result[j][i] = result[i][j];
            }

        return result;
    }

    public static int[][] GenerateRandomEuclideanCostMatrix(int vertices, int max_x, int max_y) {
        int[][] coordinates = new int[vertices][2];
        Random rng = new Random();
        for(int i = 0; i < vertices; i++) {
            coordinates[i][0] = rng.nextInt(max_x);
            coordinates[i][1] = rng.nextInt(max_y);
        }
        return CalculateCosts(vertices, coordinates);
    }

    public static int[][] GenerateRandomCircularGraphCostMatrix(int vertices, int radius) {
        int[][] coordinates = new int[vertices][2];
        Random rng = new Random();
        double angle = 2 * Math.PI / vertices;
        List<Integer> remainingVertices = new ArrayList<>();
        for(int i = 0; i < vertices; i++) remainingVertices.add(i);
        for(int i = 0; i < vertices; i++) {
            int nextVertex = remainingVertices.remove(rng.nextInt(remainingVertices.size()));
            System.out.print(nextVertex + " ");
            coordinates[nextVertex][0] = (int)(radius * Math.cos(i * angle));
            coordinates[nextVertex][1] = (int)(radius * Math.sin(i * angle));
        }
        System.out.println();
        return CalculateCosts(vertices, coordinates);
    }

    public static int[][] GetCostMatrixFromFile(String filename) {
        try {
            Scanner infile = new Scanner(new File(filename));
            List<Integer> values = new ArrayList<>();
            while(infile.hasNextInt()) values.add(infile.nextInt());
            int[][] result = new int[(int)Math.sqrt(values.size())][(int)Math.sqrt(values.size())];
            for(int i = 0; i < result.length; i++)
                for(int j = 0; j < result[0].length; j++)
                    result[i][j] = values.remove(0);
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int[][] CalculateCosts(int vertices, int[][] coordinates) {
        int[][] result = new int[vertices][vertices];

        for(int i = 0; i < vertices; i++)
            for(int j = i+1; j < vertices; j++) {
                result[i][j] = (int) Math.sqrt(
                        Math.pow(coordinates[i][0] - coordinates[j][0], 2) +
                        Math.pow(coordinates[i][1] - coordinates[j][1], 2)
                );
                result[j][i] = result[i][j];
            }

        return result;
    }

    public static int GetCost(int[][] costMatrix, int[] path) {
        int result = 0;
        for(int i = 1; i < path.length; i++)
            result += costMatrix[path[i-1]][path[i]];
        return result + costMatrix[path[path.length-1]][path[0]];
    }

    public static int[] BruteForce(int[][] costMatrix) {
        List<Integer> vertices = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        path.add(0);
        for(int i = 1; i < costMatrix.length; i++)
            vertices.add(i);
        int[] p = BruteForceRec(costMatrix,vertices,path);
        int[] result = new int[p.length+1];
        System.arraycopy(p,0, result, 0, p.length);
        result[result.length-1] = 0;
        return result;
    }

    private static int[] BruteForceRec(int[][] costMatrix, List<Integer> vertices, List<Integer> path) {
        int[] result = null;
        if(vertices.isEmpty()) {
            result = new int[path.size()];
            for(int i = 0; i < path.size(); i++)
                result[i] = path.get(i);
        } else {
            int lowestCost = -1;

            for(int i = 0; i < vertices.size(); i++) {
                path.add(vertices.remove(i));
                int[] p = BruteForceRec(costMatrix, vertices, path);
                int cost = GetCost(costMatrix, p);
                if(cost < lowestCost || lowestCost == -1) {
                    lowestCost = cost;
                    result = p;
                }
                vertices.add(i,path.remove(path.size()-1));
            }
        }
        return result;
    }

    public static int[] Greedy(int[][] costMatrix) {
        int[] path = new int[costMatrix.length+1];
        path[0] = path[path.length-1] = 0;

        List<Integer> unvisited = new ArrayList<>();
        for(int i = 1; i < costMatrix.length; i++)
            unvisited.add(i);

        for(int i = 1; !unvisited.isEmpty(); i++) {
            Integer next = null;
            int cost = -1;
            for(Integer v : unvisited) {
                if(costMatrix[path[i-1]][v] < cost || cost == -1) {
                    cost = costMatrix[path[i-1]][v];
                    next = v;
                }
            }
            path[i] = next;
            unvisited.remove(next);
        }

        return path;
    }

    public static int[] Dynamic(int[][] costMatrix) {
        List<Integer> tour = new ArrayList<>();
        for(int i = 1; i < costMatrix.length; i++)
            tour.add(i);
        int[][][] solutionTable = new int[costMatrix.length][(int) Math.pow(2, costMatrix.length)][];
        return DynamicRec(0,tour,costMatrix, solutionTable);
    }

    private static int[] DynamicRec(int start, List<Integer> tour, int[][] costMatrix, int[][][] solutionTable) {
        int ti = TourIndex(tour);
        if(solutionTable[start][ti] != null)
            return solutionTable[start][ti];

        if(tour.size() == 1)
            return solutionTable[start][ti] = new int[]{start, tour.get(0), 0};

        solutionTable[start][ti] = new int[tour.size()+2];
        int[] p = new int[tour.size()+2];
        int lowestCost = -1;
        for(int i = 0; i < tour.size(); i++){
            int vertex = tour.remove(i);
            System.arraycopy(DynamicRec(vertex, tour, costMatrix, solutionTable),0,p,1, p.length-1);
            p[0] = start;
            tour.add(i, vertex);
            int cost = GetCost(costMatrix,p);
            if(cost < lowestCost || lowestCost == -1) {
                lowestCost = cost;
                System.arraycopy(p,0,solutionTable[start][ti], 0, p.length);
            }
        }
        return solutionTable[start][ti];
    }

    private static int TourIndex(List<Integer> tour) {
        int result = 0;
        for(Integer v : tour)
            result += Math.pow(2,v);
        return result;
    }

    /** Get CPU time in nanoseconds since the program(thread) started. */
    /** from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/
    public static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ?
                bean.getCurrentThreadCpuTime() : 0L;
    }

    public static long factorial(long x) {
        return x < 2 ? 1 : x * factorial(x-1);
    }
}
