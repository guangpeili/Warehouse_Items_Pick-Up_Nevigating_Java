package Projects;

public class Node {
	
	//x and y coordinate of the node in the grid
	public int x;
	public int y;
	
	
	//In A* search, we have f = h + g
	public int h;
	public int g;
	public int f;
	
	
	//Whether this node is blocked, 1 for yes and 0 for no
	public boolean blocked;
	
	
	//Used to build the path
	public Node source;
	
	
	//Constructor of the class
	public Node(int x, int y, boolean blocked) {
		this.x = x;
		this.y = y;
		this.blocked = blocked;
	}
	
}
