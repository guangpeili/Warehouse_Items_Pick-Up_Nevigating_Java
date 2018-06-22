package Projects;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import com.opencsv.CSVReader;
import edu.princeton.cs.introcs.*;

import com.opencsv.CSVReader;

import edu.princeton.cs.introcs.StdDraw;

public class BnbBatch {

	
	//Number of columns and rows of the 2D array
	static int xLength = 36; //18*2
	static int yLength = 20; //10*2
	static ArrayList<Node> completePath = new ArrayList<>();
	//Integer distance between single pair of nodes in warehouse
	static int totalDist;
	static double totalCost;
	static double totalWeight;
	static ArrayList<int[]> grid;
	static ArrayList<double[]> itemDetail;
	static ArrayList<String[]> result;	
		
	public static void main(String[] args) {
		
		result = new ArrayList<String[]>();
		
		/*
		 * Start reading data from given CSV file
		 * However, numbers would be stored in Strings
		 * Later process is required to convert to Integers
		 */
		CSVReader csvReader = null;
		//This would store the data from the CSV file
		List<String[]> mapDataString = new ArrayList<String[]>(); 
		
		try { //Read data using OpenCSV library
			FileReader fileReader = new FileReader("warehouse-grid.csv");
			csvReader = new CSVReader(fileReader, ',', '"', 0);
			//Represent warehouse grid in String format
			mapDataString = csvReader.readAll();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				csvReader.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
		//Warehouse grid information stored in integers, with x, y coordinates multiplied by 2
		ArrayList<double[]> gridDouble = StringToDouble(mapDataString);
		grid  = DoubleToInt(gridDouble);
		gridDouble.clear();
		
		
		//This would be the 2-dimensional grid where we'll perform the search
		Node[][] map = new Node[xLength+3][yLength+3];
		
		
		//Create the map frame where all cells are set to non-blocked
		for (int i = 0; i <= xLength + 2; i++) {
			for (int j = 0; j <= yLength + 2; j++) {
				map[i][j] = new Node(i, j, false);
			}
		}
		
		
		//Create ArrayList (string) to store the item details
		List<String[]> itemDetailString = new ArrayList<String[]>();
		
		
		//Import item details from given CSV file
		try {
			FileReader fileReader = new FileReader("item-details.csv");
			csvReader = new CSVReader(fileReader, ',', '"', 0);
			itemDetailString = csvReader.readAll();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				csvReader.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
		//Convert the String type to doubles
		int itemDetailLength = itemDetailString.size();
	
		itemDetail = new ArrayList<double[]>();
		
		for (int i = 0; i < itemDetailLength; i++) {
			double[] row = new double[5];
			String[] s = itemDetailString.get(i);
			for (int j = 0; j < 5; j++) {
				String str = s[j];
				double value = Double.parseDouble(str);
				row[j] = value;
			}
			itemDetail.add(row);
		}
		
		
		//Clear the original string data
		itemDetailString.clear();
	
		
		//Set the BLOCKED variable at shelves position to TRUE
		for (int i = 1; i <= xLength + 1; i = i + 2) {
			for (int j = 1; j <= yLength + 1; j = j + 2) {
				if (!(i == 1 && j == 1))
					map[i][j].blocked = true;
			}
		}
				
		
		//Again use OpenCSV to read order data from given file
		List<String[]> orderDataString = new ArrayList<String[]>();
				
		try { //Read data using OpenCSV library
			FileReader fileReader = new FileReader("warehouse-orders-v02.csv");
			csvReader = new CSVReader(fileReader, ',', '"', 0);
			//Represent warehouse grid in String format
			orderDataString = csvReader.readAll();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				csvReader.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
				
		int orderNum = orderDataString.size();
				
	
		//Then convert data type from String to Integers
		ArrayList<int[]> orderDataInt = new ArrayList<int[]>();
		for (int i = 0; i < orderNum; i++) {
			String[] row = orderDataString.get(i);
			int n = 0;
					
			for (int j = 0; j < row.length; j++) {
				if (!StringUtils.isEmpty(row[j])) 
					n++;
			}
					
			int[] buffer = new int[n];
					
			for (int j = 0; j < n; j++) {
				String str = row[j];
				int intt = Integer.parseInt(str);
				buffer[j] = intt;
			}
					
			orderDataInt.add(buffer);
		}
				
		
		//Dump the original String data to save memory
		orderDataString.clear();
				
		/*		
		//Let user input which order to pick up
		int targetOrder;
		System.out.println("Please Enter Order Number:");
		Scanner in = new Scanner(System.in);
		
		//In the given file, order number starts from 1 rather than 0
		targetOrder = in.nextInt() - 1;
				
		//Examine if the input is a feasible order number
		while (targetOrder < 0 || targetOrder > 99) {
			System.out.println("Invalid Order Number. Enter Another One");
			Scanner inn = new Scanner(System.in);
			targetOrder = in.nextInt() - 1;
		}
		*/
		
		
		for (int a = 0; a < 2500; a++) {
		int targetOrder = a;
		completePath.clear();
		totalDist = 0;
		System.out.println(a);
		//Retrieve order information about its contained items
		int[] orderInfo = orderDataInt.get(targetOrder);
		//Compute how many items are there in the particular order
		int numItem = orderInfo.length;
				
		
		//Do not do batch processing if having item number larger than 10
		if (numItem > 15) 
			continue;
		/*
		 * Starting and Ending Locations
		 */
		int xStart, yStart, xEnd, yEnd;
		xStart = 0;
		yStart = 0;
		xEnd = 38;
		yEnd = 22;
		/*
		//Intrigue use to input starting and ending position
		System.out.println("Please Enter Starting x Coordinate:");
		Scanner in1 = new Scanner(System.in);
		xStart = in1.nextInt();
		System.out.println("Please Enter Starting y Coordinate:");
		Scanner in2 = new Scanner(System.in);
		yStart = in2.nextInt();
				
		
		//Judge if the starting position is legal, if not, re-input
		while (map[xStart][yStart].blocked || xStart < 0 || xStart > 38 || yStart < 0 || yStart > 22) {
			System.out.println("Illegal Start Location. Select Another Location");
			System.out.println("Please Enter Starting x Coordinate:");
			Scanner in11 = new Scanner(System.in);
			xStart = in11.nextInt();
			System.out.println("Please Enter Starting y Coordinate:");
			Scanner in22 = new Scanner(System.in);
			yStart = in22.nextInt();
		}
				
		System.out.println("Please Enter Ending x Coordinate:");
		Scanner in3 = new Scanner(System.in);
		xEnd = in3.nextInt();
		System.out.println("Please Enter Ending y Coordinate:");
		Scanner in4 = new Scanner(System.in);
		yEnd = in4.nextInt();
				
				
		//Judge if the ending position is legal, if not, re-input
		while (map[xEnd][yEnd].blocked || xEnd < 0 || xEnd > 38 || yEnd < 0 || yEnd > 22) {
			System.out.println("Illegal End Location. Select Another Location");
			System.out.println("Please Enter Ending x Coordinate:");
			Scanner in33 = new Scanner(System.in);
			xStart = in33.nextInt();
			System.out.println("Please Enter Ending y Coordinate:");
			Scanner in44 = new Scanner(System.in);
			yStart = in44.nextInt();
		}
		*/		
				
		//Create a List to store an open list of all items
		List<Integer> stack = new ArrayList<>();
		for (int i = 0; i < numItem; i++) {
			stack.add(orderInfo[i]);
		}
				
		
		//Clear the old order data 
		orderInfo = null;
			
		
		//Create a matrix to store the distance of all pairs in that order
		int[][] dist = new int[numItem][numItem];

		for (int i = 0; i < numItem; i++) {
			//Distance between the same item has infinity length
			dist[i][i] = -1;
			for (int j = i + 1; j < numItem; j++) {
				int[] s = getCoordinate(stack.get(i));
				int[] t = getCoordinate(stack.get(j));
				int distance = Math.abs(t[0] - s[0]) + Math.abs(t[1] - s[1]);
				dist[i][j] = distance;
				dist[j][i] = distance;
			}
		}
				
		
		//Find the first node to visit after leaving start location
				
		//Initialize the cost function
		totalCost = 0;
		totalWeight = 0;
				
		
		//Record if all the items' weight are available
		boolean complete = true;
				
		int indexNow = 0;
		int minDist = Integer.MAX_VALUE;
		for (int i = indexNow; i < numItem; i++) {
			int[] buffer = getCoordinate(stack.get(i));
			int temp = buffer[0] + buffer[1];
			if (temp < minDist) {
				indexNow = i;
				minDist = temp;
			}
		}
				
				
				//State we are accessing from the which side of the item, true for left and false for right
				//Default side of first item would be left, since the default starting location would be the origin
				boolean side = true;
				
				
				//Examine if there is no item to the left of the first item, but there is one located to the right of it
				//If so, change the side to the right
				int[] nowCoordinate = getCoordinate(stack.get(indexNow));
				int[] checkLeft = {nowCoordinate[0] - 2, nowCoordinate[1]};
				int[] checkRight = {nowCoordinate[0] + 2, nowCoordinate[1]};
				boolean left = false;
				boolean right = false;
				
				for (int i = 0; i < numItem; i++) {
					if (Arrays.equals(getCoordinate(stack.get(i)), checkLeft)) 
						left = true;
					else if (Arrays.equals(getCoordinate(stack.get(i)), checkRight)) 
						right = true;
				}
				
				if (!left && right)
					side = false;
				
				int xt = 0, yt = 0;
				
				
				//Find path from starting point to the first node
				if (side) {
					xt = getCoordinate(stack.get(indexNow))[0] - 1;
					yt = getCoordinate(stack.get(indexNow))[1];
				} else if (!side) {
					xt = getCoordinate(stack.get(indexNow))[0] + 1;
					yt = getCoordinate(stack.get(indexNow))[1];
				}
				
				
				double weight = getWeight(stack.get(indexNow));
				
				if (weight == -1) {
					complete = false;
				} else {
					totalWeight = totalWeight + weight;
				}
				
				
				//Compute the path from the starting location to the first item
				computeH(map, xStart, yStart, xt, yt);
				
				int xs = xt;
				int ys = yt;
				
				
				//Create a visited list for items that has already been picked up
				List<Integer> picked = new ArrayList<>();
				picked.add(stack.get(indexNow));
				
				
				//Create the upper bound by greedy algorithm to save running time
				int ub = Integer.MAX_VALUE;
				List<Integer> finalPath = new ArrayList<Integer>();
				
			
				//Start reducing cost matrix
				int bnbCost = subtractMin(dist);
				
				
				//Create the root node matrix
				Matrices root = new Matrices(dist);
				root.index = indexNow;
				root.path = new ArrayList<Integer>();
				root.path.add(indexNow);
				root.cost = bnbCost;
				
				
				//Create a stack to perform the Depth-first-search
				Stack<Matrices> bnbSpace = new Stack<Matrices>();
				//Then add the root node matrix to this stack
				bnbSpace.push(root);
				
				
				List<Integer> currentPath;
				int numItemVisited;
				int currentCost;
				
				
				//Start the branch and bound searching
				while (true) {

					//Stop if the whole search space has been explored 
					if (bnbSpace.empty()) 
						break;
					
				
					//Removes the current node from the stack and adds to explored list
					Matrices current = bnbSpace.pop();
					
					currentPath = current.path;
					
					numItemVisited = currentPath.size();
					currentCost = current.cost;
					
					indexNow = current.index;
					
					
					//If the current cost is larger than the upper bound, then prune this path
					if (currentCost >= ub) 
						continue;
						
					
					//Check whether all items have been visited
					if (numItemVisited == numItem) {
						
						//Update the value of upper bound
						if (current.cost < ub) {
							ub = current.cost;
							finalPath = current.path;
						}
						
					} else if (numItemVisited < numItem) {
						
						//Create a list to store the unvisited items
						List<Integer> unexplored = new ArrayList<Integer>();
						
						for (int i = 0; i < numItem; i++) {
							if (!current.path.contains(i)) {
								unexplored.add(i);
							}
						}
						
						
						//Number of items left to visit
						int numItemUnexplored = unexplored.size();
						int[][] buffer = new int[numItem][numItem];
						
						
						//Keep reducing matrices and add obtained children to the stack
						for (int i = 0; i < numItemUnexplored; i++) {
							
							//Extract the matrix of the current node
							buffer = cloneMatrix(current.matrix);
							
							
							//Get the value of node "[A, B]"
							int extra1;
							if (buffer[indexNow][unexplored.get(i)] == -1)
								extra1 = 0;
							else
								extra1 = buffer[indexNow][unexplored.get(i)];
							
							
							//Set the whole row of current index to infinity
							for (int j = 0; j < numItem; j++) {
								buffer[indexNow][j] = -1;
							}
							
							
							//Set the whole column of next index to infinity
							for (int k = 0; k < numItem; k++) {
								buffer[k][unexplored.get(i)] = -1;
							}
		
							
							//Set node "[B, A]" to infinity
							buffer[unexplored.get(i)][indexNow] = -1;
							
							//Reduce matrix buffer
							int extra2 = subtractMin(buffer);
							
							bnbCost = currentCost + extra1 + extra2;
							
							List<Integer> addItem = cloneArrayList(current.path);
							addItem.add(unexplored.get(i));
							
							//Push the new obtained Matrices to stack
							bnbSpace.push(new Matrices(buffer, unexplored.get(i), addItem, bnbCost));
							
						}
						
						
					} else if (numItemVisited > numItem) {
						
						//If the algorithm goes correctly, this situation will not occur
						System.out.println("Error Occurs!");
						
					}
					
					
				}
				
				
				
				//Compute the rest of paths 
				while (true) {
					
					//index in the final path
					int index = 1;
					
					
					//Find out if there're other items which can be also picked at current position
					//If the current position is at the left-hand-side of the current item, then only right-hand-side of some other items may also be picked at this position
					//if the current position is at the right-hand-side of the current item, then only left-hand-side of some other items may also be picked at this position
					if (side) {
						for (int i = 0; i < numItem; i++) {
							if (i != indexNow) {
								if ((getCoordinate(stack.get(i))[0] + 1) == xs && getCoordinate(stack.get(i))[1] == ys && !picked.contains(stack.get(i))) {
									picked.add(stack.get(i));
									
									weight = getWeight(stack.get(i));
									if (complete && weight != -1) {
										totalWeight = totalWeight + weight;
									} else if (weight == -1) {
										complete = false;
									}
								}
							}
						}
					} else if (!side) {
						for (int i = 0;  i < numItem; i++) {
							if (i != indexNow) {
								if((getCoordinate(stack.get(i))[0] - 1) == xs && getCoordinate(stack.get(i))[1] == ys && !picked.contains(stack.get(i))) {
									picked.add(stack.get(i));
									
									weight = getWeight(stack.get(i));
									if (complete && weight != -1) {
										totalWeight = totalWeight + weight;
									} else if (weight == -1) {
										complete = false;
									}
								}
							}
						}
					}
					
					
					//Check if there are multiple items located at the same position
					for (int i = 0; i < numItem; i++) {
						if (i != indexNow) {
							if (Arrays.equals(getCoordinate(stack.get(i)), getCoordinate(stack.get(indexNow))) && !picked.contains(stack.get(i))) {
								picked.add(stack.get(i));
								
								weight = getWeight(stack.get(i));
								if (complete && weight != -1 ) {
									totalWeight = totalWeight + weight;
								} else if (weight == -1) {
									complete = false;
								}
							}
						}
					}
					
					
					// If all items have been picked, then terminate
					if (picked.size() == numItem)
						break;
					
					
					//Find the next index to visit
					int indexNext = finalPath.get(index);
					
					
					//If the next index has already been picked up, continue
					while (picked.contains(stack.get(indexNext))) {
						index++;
						if (index < numItem)
							indexNext = finalPath.get(index);
						else 
							break;
					}
					
					
					//Determine the side from current node to next one
					if (xs < getCoordinate(stack.get(indexNext))[0]) {
						side = true;
					} else {
						side = false;
					}
					
					
					//Again check whether going to left or right side is optimal here
					nowCoordinate = getCoordinate(stack.get(indexNext));
					checkLeft[0] = nowCoordinate[0] - 2;
					checkLeft[1] = nowCoordinate[1];
					checkRight[0] = nowCoordinate[0] + 2;
					checkRight[1] = nowCoordinate[1];
					left = false;
					right = false;
					
					
					if (side) {
						for (int i = 0; i < numItem; i++) {
							if (Arrays.equals(getCoordinate(stack.get(i)), checkLeft) && !picked.contains(stack.get(i)))
								left = true;
							else if (Arrays.equals(getCoordinate(stack.get(i)), checkRight) && !picked.contains(stack.get(i)))
								right = true;
						}
						
						if (!left && right)
							side = false;
					} else if (!side) {
						for (int i = 0; i < numItem; i++) {
							if (Arrays.equals(getCoordinate(stack.get(i)), checkLeft) && !picked.contains(stack.get(i)))
								left = true;
							else if (Arrays.equals(getCoordinate(stack.get(i)), checkRight) && !picked.contains(stack.get(i)))
								right = true;
						}
						
						if (!right && left)
							side = true;
					}
					
					
					//Then determine the coordinate of item visiting next
					if (side) 
						xt = getCoordinate(stack.get(indexNext))[0] - 1;
					else if (!side) 
						xt = getCoordinate(stack.get(indexNext))[0] + 1;
					
					yt = getCoordinate(stack.get(indexNext))[1];
					
					
					//Generate the path
					computeH(map, xs, ys, xt, yt);
					
					//Add next item to picked list
					picked.add(stack.get(indexNext));
					
					weight = getWeight(stack.get(indexNext));
					
					if (complete && weight != -1) {
						totalWeight = totalWeight + weight;
					} else if (weight == -1) {
						complete = false;
					}
					
					//Move item from current to next
					indexNow = indexNext;
					xs = xt;
					ys = yt;
					
				}
				
				
				//Add path from the last item to dropping location
				computeH(map, xs, ys, xEnd, yEnd);
				
				/*
				//Draw the path 
				visualize(map, completePath, xStart, yStart, xEnd, yEnd, stack, numItem);
				
				System.out.println();
				System.out.println("----------------------------------------------------------------------------");
				System.out.println();
				
				
				//Display the order of items
				System.out.print("The visiting order of items is: ");
				for (int i = 0; i < numItem; i++) {
					System.out.print(stack.get(finalPath.get(i)) + " ");
				}
				System.out.println();
				System.out.println();
				
				
				//Display total distance 
				System.out.format("The toal distance of the generated path is: %d", totalDist);
				System.out.println();
				System.out.println();
				
				//Display total cost
				if (complete) {
					System.out.format("The total cost of the generated path is: %.2f", totalCost);
					System.out.println();
				} else {
					System.out.println("The weight information of involved item(s) is not complete, total cost cannot be generated");
					System.out.println();
				}
				
				*/
				String[] ss = addArrow(removeDuplicate(completePath), a + 1, totalDist);
				result.add(ss);
				
		}
		
		CsvWriter writer = new CsvWriter();
		
		writer.exportCsv(result, "bnbBatch.txt");
	
	}
	
	
	
	
	
	
	/**
	 * Convert all Strings to Doubles
	 */
	public static ArrayList<double[]> StringToDouble(List<String[]> mapDataString) {
		int n = mapDataString.size();
		ArrayList<double[]> grid = new ArrayList<double[]>();
		for(int i = 0; i < n; i++) {
			double[] row = new double[3];
			String[] s = mapDataString.get(i);
			for(int j = 0; j < 3; j++) {
				String str = s[j];
				double value = Double.parseDouble(str);
				row[j] = value;
			}
			grid.add(row);
		}
		
		return grid;
	}
	
	
	/**
	 * Convert all Doubles to Integers 
	 */
	public static ArrayList<int[]> DoubleToInt(ArrayList<double[]> mapDataDouble) {
		int n = mapDataDouble.size();
		ArrayList<int[]> grid = new ArrayList<int[]>();
		
		for(int i = 0; i < n; i++) {
			int[] row = new int[3];
			double[] d = mapDataDouble.get(i);
			for(int j = 0; j < 3; j++) {
				double db = d[j];
				int value = (int)db;
				//Multiply the value of each coordinate by 2
				if (j > 0)
					value = value*2 + 1;
				row[j] = value;
			}
			grid.add(row);
		}
		
		return grid;
	}
	
	
	/*
	 * Compute the estimate distance (h value) of A* search 
	 */
	public static void computeH(Node[][] map, int xStart, int yStart, int xEnd, int yEnd) {
		
		for (int i = 0; i <= xLength + 2; i++) {
			for (int j = 0; j <= yLength + 2; j++) {
				//Compute the heuristic by Manhattan Distance if current cell is not a shelf
				if (!map[i][j].blocked)
					map[i][j].h = Math.abs(i - xEnd) + Math.abs(j - yEnd);
				else
					map[i][j].h = -1;
			}
		}
		computePath(map, xStart, yStart, xEnd, yEnd);
	}
	
	
	public static void computePath(Node[][] map, int xStart, int yStart, int xEnd, int yEnd) {
		
		ArrayList<Node> pathList = new ArrayList<Node>();
		ArrayList<Node> singlePath = new ArrayList<Node>();
		
		//Clear singlePath & pathList so as to prepare for the next set of computation
		pathList.clear();
		singlePath.clear();
		
		//Clear all parent-child relationship
		
		
		int pairDist = 0;
		
		//Create a priority queue and design the comparator with f value
		PriorityQueue<Node> openList = new PriorityQueue<>(11, new Comparator() {
			@Override
			public int compare(Object n1, Object n2) {
				if (((Node)n1).f < ((Node)n2).f)
					return -1;
				else if (((Node)n1).f > ((Node)n2).f)
					return 1;
				else
					return 0;
			}
		});
		
		//Add the start node into the openList
		openList.add(map[xStart][yStart]);
		
		//While there're still cells left to be processed, keep running
		while (true) {
			
			//Retrieve and remove the first element from priority queue
			//which is the one with smallest f value
			Node current = openList.poll();
			
			//Break if the openList is already empty
			if (current == null)
				break;
			
			//Check whether the current is the destination node
			if (current == map[xEnd][yEnd]) {
				singlePath.add(current);
				break;
			}
		
			singlePath.add(current);
			pairDist++;
			
			//Clear the openList to store search nodes of next round
			openList.clear();
			
			//Start searching with the current node's left neighbor
			try {
				if (map[current.x - 1][current.y].h != -1
						&& !openList.contains(map[current.x - 1][current.y])
						&& !singlePath.contains(map[current.x - 1][current.y])
						&& current.x - 1 >= 0 && current.x - 1 <= xLength + 2
						&& current.y >= 0 && current.y <= yLength + 2) {
					int preCost = current.g + 1;
					map[current.x - 1][current.y].g = preCost;
					int fValue = map[current.x - 1][current.y].h + preCost;
					
					if (map[current.x - 1][current.y].f > fValue 
							|| !openList.contains(map[current.x - 1][current.y]))
						map[current.x - 1][current.y].f = fValue;
					
					openList.add(map[current.x - 1][current.y]);
					map[current.x - 1][current.y].source = current;
				}
			} catch (IndexOutOfBoundsException e) {}
			
			//Continue searching right neighbor
			try {
				if (map[current.x + 1][current.y].h != -1
						&& !openList.contains(map[current.x + 1][current.y])
						&& !singlePath.contains(map[current.x + 1][current.y])
						&& current.x + 1 >= 0 && current.x + 1 <= xLength + 2
						&& current.y >= 0 && current.y <= yLength + 2) {
					int preCost = current.g + 1;
					map[current.x + 1][current.y].g = preCost;
					int fValue = map[current.x + 1][current.y].h + preCost;
					
					if (map[current.x + 1][current.y].f > fValue 
							|| !openList.contains(map[current.x + 1][current.y]))
						map[current.x + 1][current.y].f = fValue;
					
					openList.add(map[current.x + 1][current.y]);
					map[current.x + 1][current.y].source = current;
				}
			} catch (IndexOutOfBoundsException e) {}
			
			//Continue searching upper neighbor
			try {
				if (map[current.x][current.y + 1].h != -1
						&& !openList.contains(map[current.x][current.y + 1])
						&& !singlePath.contains(map[current.x][current.y + 1])
						&& current.x >= 0 && current.x <= xLength + 2
						&& current.y + 1 >= 0 && current.y + 1 <= yLength + 2) {
					int preCost = current.g + 1;
					map[current.x][current.y + 1].g = preCost;
					int fValue = map[current.x][current.y + 1].h + preCost;
					
					if (map[current.x][current.y + 1].f > fValue 
							|| !openList.contains(map[current.x][current.y + 1]))
						map[current.x][current.y + 1].f = fValue;
					
					openList.add(map[current.x][current.y + 1]);
					map[current.x][current.y + 1].source = current;
				}
			} catch (IndexOutOfBoundsException e) {}
			
			//Continue searching lower neighbor
			try {
				if (map[current.x][current.y - 1].h != -1
						&& !openList.contains(map[current.x][current.y - 1])
						&& !singlePath.contains(map[current.x][current.y - 1])
						&& current.x >= 0 && current.x <= xLength + 2
						&& current.y - 1 >= 0 && current.y - 1 <= yLength + 2) {
					int preCost = current.g + 1;
					map[current.x][current.y - 1].g = preCost;
					int fValue = map[current.x][current.y - 1].h + preCost;
					
					if (map[current.x][current.y - 1].f > fValue 
							|| !openList.contains(map[current.x][current.y - 1]))
						map[current.x][current.y - 1].f = fValue;
					
					openList.add(map[current.x][current.y - 1]);
					map[current.x][current.y - 1].source = current;
				}
			} catch (IndexOutOfBoundsException e) {}
		}
		
		//Cause we add extra one distance for starting node, need to subtract it now
		totalDist += pairDist;
		completePath.addAll(singlePath);
		
		totalCost = totalCost + totalWeight * pairDist;
	}
	
	
	//Get the x and y coordinate of an item with its serial number using binary search
	public static int[] getCoordinate(int item) {
		int length = grid.size();
		int low = 0;
		int high = length - 1;
		int[] coordinate = new int[2];
		
		while (low <= high) {
			int mid = low + (high - low)/2; 
			
			if (grid.get(mid)[0] == item) {
				coordinate[0] = grid.get(mid)[1];
				coordinate[1] = grid.get(mid)[2];
				break;
			} else if (grid.get(mid)[0] > item){
				high = mid - 1;
			} else if (grid.get(mid)[0] < item){
				low = mid + 1;
			}
		}
		return coordinate; 
	}
	
	
	// Get the weight of one specific item, if there is no data, return -1
	public static double getWeight(int item) {
		
		int length = itemDetail.size();
		int low = 0;
		int high = length - 1;
		double weight = -1;
		
		while (low <= high) {
			int mid = low + (high - low) / 2;
			
			if (itemDetail.get(mid)[0] == item) {
				weight = itemDetail.get(mid)[4];
				break;
			} else if (itemDetail.get(mid)[0] > item) {
				high = mid - 1;
			} else if (itemDetail.get(mid)[0] < item) {
				low = mid + 1;
			}
		}
		
		return weight;
	}

	/*
	public static void visualize(Node[][] map, ArrayList<Node> completePath, int xStart, int yStart, int xEnd, int yEnd, List<Integer> order, int numItem) {
		
		StdDraw.setXscale(-2, xLength + 4);
		StdDraw.setYscale(-2, yLength + 4);
		
		//Set up the grid, the black cells are marked as shelves 
		StdDraw.setPenColor(StdDraw.BLACK);
		for (int i = 0; i <= xLength + 2; i++) {
			for (int j = 0; j <= yLength + 2; j++) {
				if (map[i][j].h == -1) { 
					StdDraw.square(i, j, 0.5);
					StdDraw.filledSquare(i, j, 0.5);
				} else
					StdDraw.square(i, j, 0.5);
			}
		}
		
		//Mark the location of items in this order as Blue
		StdDraw.setPenColor(StdDraw.BLUE);
		for (int i = 0; i < numItem; i++) {
			int x = getCoordinate(order.get(i))[0];
			int y = getCoordinate(order.get(i))[1];
			StdDraw.filledSquare(x, y, 0.5);
		}
		
		//Plot the path
		StdDraw.setPenColor(StdDraw.YELLOW);
		int length = completePath.size();
		for (int i = 0; i < length; i++) {
			int x = completePath.get(i).x;
			int y = completePath.get(i).y;
			StdDraw.filledSquare(x, y, 0.5);
		}
		
		//Draw the starting and dropping location, marked as red
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.filledSquare(xStart, yStart, 0.5);
		StdDraw.filledSquare(xEnd, yEnd, 0.5);
	}
	*/
	
	//Subtracting row and column minimum from the cost matrix
	public static int subtractMin(int[][] cost) {
		
		int length = cost.length;
		int minSum = 0;
		int rowMin = Integer.MAX_VALUE;
		int colMin = Integer.MAX_VALUE;
		
		//Subtract row minimum
		for (int i = 0; i < length; i++) {
			
			for (int j = 0; j < length; j++) {
				if (cost[i][j] != -1 && cost[i][j] < rowMin) {
					rowMin = cost[i][j];
				}
				
				if (cost[i][j] == 0) {
					rowMin = 0;
					break;
				}
			}
			
			if (rowMin == Integer.MAX_VALUE) {
				rowMin = 0;
			}
			
			minSum += rowMin;
			
			if (rowMin != 0) {
				for (int j = 0; j < length; j++) {
					if (cost[i][j] != -1) {
						cost[i][j] -= rowMin;
					}
				}
			}
			
			rowMin = Integer.MAX_VALUE;
		}
		
		
		//Subtract column minimum
		for (int j = 0; j < length; j++) {
			
			for (int i = 0; i < length; i++) {
				if (cost[i][j] != -1 && cost[i][j] < colMin) {
					colMin = cost[i][j];
				}
				
				if (cost[i][j] == 0) {
					colMin = 0;
					break;
				}
			}
			
			if (colMin == Integer.MAX_VALUE) {
				colMin = 0;
			}
			
			minSum += colMin;
			
			if (colMin != 0) {
				for (int i = 0; i < length; i++) {
					if (cost[i][j] != -1) {
						cost[i][j] -= colMin;
					}
				}
			}
			
			colMin = Integer.MAX_VALUE;
		}
		
		return minSum;
	}
	
	
	//Make a duplicate of an ArrayList
	public static ArrayList<Integer> cloneArrayList(List<Integer> arraylist) {
		
		ArrayList<Integer> newList = new ArrayList<Integer>();
		
		int n = arraylist.size();
		
		for (int i = 0; i < n; i++) {
			newList.add(arraylist.get(i));
		}
		
		return newList;
	}
	
	
	//Make a duplicate of a matrix
	public static int[][] cloneMatrix(int[][] matrix) {
		
		int n = matrix.length;
		
		int[][] newMatrix = new int[n][n];
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				newMatrix[i][j] = matrix[i][j];
			}
		}
		
		return newMatrix;
	}
	
	
	//Delete duplicate consecutive items in an Array List
	public static ArrayList<int[]> removeDuplicate(ArrayList<Node> list) {
		
		int n = list.size();
		
		ArrayList<int[]> buffer = new ArrayList<int[]>();
		
		for (int i = 0; i < n; i++) {
			int[] row = new int[2];
			row[0] = list.get(i).x;
			row[1] = list.get(i).y;
			buffer.add(row);
		}
		
		ArrayList<int[]> newList = new ArrayList<int[]>();
		newList.add(buffer.get(0));
		int old = 0;
		
		for (int i = 1; i < n; i++) {
			if (!Arrays.equals(buffer.get(i), buffer.get(old))) {
				newList.add(buffer.get(i));
				old = i;
			}
		}
		
		return newList;
	}
	
	
	//Add arrow head symbol between coordinates
	public static String[] addArrow(ArrayList<int[]> list, int a, int b) {
		
		int n = list.size();
		
		String[] ss = new String[n + 2];
		
		ss[0] = "Order No." + a + "  ";
		ss[1] = "Total Distance: " + b + "     ";
		
		
		for (int i = 0; i < n - 1; i++) {
			String s = "(" + list.get(i)[0] + ", " + list.get(i)[1] + ") → ";
			ss[i + 2] = s;
		}
		
		String s = "(" + list.get(n - 1)[0] + ", " + list.get(n - 1)[1] + ") → END";
		ss[n + 1] = s;
		
		return ss;
	}	
		
}
