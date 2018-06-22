package Projects;

import java.util.List;

public class Matrices {

	//This stores the reduced matrix→
	public int[][] matrix;
	
	
	public List<Integer> path;
	
	
	//This variable denotes which item the current Tree Node represents
	public int index;
	
	
	//This stores the current cost of the path
	public int cost;

	
	//Constructor of the class
	public Matrices(int[][] matrix) {
		this.matrix = matrix;
	}
	
	
	//Constructor of the class
	public Matrices(int[][] matrix, int index, List<Integer> path, int cost) {
		this.matrix = matrix;
		this.index = index;
		this.path = path;
		this.cost = cost;
	}
	
}
