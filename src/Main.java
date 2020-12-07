import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

    }

    public double[][] GenerateRandomCostMatrix(int vertices, int max_edge_cost) {
        double[][] result = new double[vertices][vertices];
        Random rng = new Random();

        for(int i = 0; i < vertices; i++)
            for(int j = i; j < vertices; j++) {
                result[i][j] = i == j ? 0 : rng.nextInt(max_edge_cost);
                result[j][i] = result[i][j];
            }

        return result;
    }

    public double[][] GenerateRandomEuclideanCostMatrix(int vertices, int max_x, int max_y) {
        double[][] coordinates = new double[vertices][2];
        Random rng = new Random();
        for(int i = 0; i < vertices; i++) {
            coordinates[i][0] = rng.nextInt(max_x);
            coordinates[i][1] = rng.nextInt(max_y);
        }
        return CalculateCosts(vertices, coordinates);
    }

    public double[][] GenerateRandomCircularGraphCostMatrix(int vertices, int N, int radius) {
        double[][] coordinates = new double[vertices][2];
        Random rng = new Random();
        double angle = 2 * Math.PI / vertices;
        List<Integer> remainingVertices = new ArrayList<>();
        for(int i = 0; i < vertices; i++) remainingVertices.add(i);
        for(int i = 0; i < vertices; i++) {
            int nextVertex = remainingVertices.remove(rng.nextInt(remainingVertices.size()));
            coordinates[nextVertex][0] = radius * Math.cos(i * angle);
            coordinates[nextVertex][1] = radius * Math.sin(i * angle);
        }
        return CalculateCosts(vertices, coordinates);
    }

    private double[][] CalculateCosts(int vertices, double[][] coordinates) {
        double[][] result = new double[vertices][vertices];

        for(int i = 0; i < vertices; i++)
            for(int j = i; j < vertices; j++) {
                result[i][j] = Math.sqrt(Math.pow(
                        coordinates[i][0] - coordinates[j][0], 2) +
                        Math.pow(coordinates[i][0] - coordinates[j][0], 2)
                );
                result[j][i] = result[i][j];
            }

        return result;
    }

    // Brute Force
    // Dynamic programming
    // Greedy Algorithm
    // The ACS or "Ant Colony" algorithm
    // A "genetic algorithm"
}
