import java.util.Arrays;

public class Verification {
    public static void main(String[] args) {
        int num_vertex = 5, radius = 4;

        // I made this function also print the order than it places the vertices
        int[][] CircularCostMatrix = Main.GenerateRandomCircularGraphCostMatrix(num_vertex, radius);

        int[] brute_path = Main.BruteForce(CircularCostMatrix);
        int[] dynamic_path = Main.Dynamic(CircularCostMatrix);
        int[] greedy_path = Main.Greedy(CircularCostMatrix);

        System.out.println(Arrays.toString(brute_path));
        System.out.println(Arrays.toString(dynamic_path));
        System.out.println(Arrays.toString(greedy_path));
    }
}
