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
import edu.princeton.cs.introcs.StdDraw;

public class Reordering {

	static ArrayList<double[]> itemDetail;
	
	public static void main(String[] args) {
		
		System.out.println("You will be indicating your own weight and item number limit for the reordering process");
		
		
		System.out.println("Enter the weight limit (in lb):");
		Scanner in1 = new Scanner(System.in);
		int weightLimit = in1.nextInt();
		
		
		System.out.println("Enter the item number limit:");
		Scanner in2 = new Scanner(System.in);
		int numLimit = in1.nextInt();
		
		
		//Array List to store order information 
		List<String[]> orderDataString = new ArrayList<String[]>();
		CSVReader csvReader = null;
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
		
		
		//Create Array List to store the reordered order
		List<String[]> reorderData = new ArrayList<String[]>();
		
		int numSum = 0;
		int weightSum = 0;
		
		List<String> reorder = new ArrayList<String>();
		
		for (int i = 0; i < orderNum; i++) {
			
			int weightSumBuffer = 0;
			int numSumBuffer = 0;
			
			int[] buffer = orderDataInt.get(i);
			int num = buffer.length;
			double weight;
			
			numSum += num;
			numSumBuffer += num;
			
			for (int j = 0; j < num; j++) {
				
				weight = getWeight(buffer[j]);
				
				if (weight == -1) 
					weight = 0;
				
				weightSum += weight;
				weightSumBuffer += weight;
			}
			
			if (numSum < numLimit && weightSum < weightLimit) {
				
				for (int k = 0; k < num; k++) {
					String s = buffer[k] + " ";
					reorder.add(s);
				}
				
			} else {
				
				int n = reorder.size();
				String[] ss = new String[n];
				
				for (int k = 0; k < n; k++) {
					ss[k] = reorder.get(k);
				}
				
				reorderData.add(ss);
				
				reorder.clear();
				
				for (int k = 0; k < num; k++) {
					String s = buffer[k] + " ";
					reorder.add(s);
				}
				
				weightSum = weightSumBuffer;
				numSum = numSumBuffer;
				
			}
			
		}
		
		System.out.println("Process finished!");
		System.out.println();
		System.out.format("Number of order generated is: %d", reorderData.size());
		
		CsvWriter writer = new CsvWriter();
		
		writer.exportCsv(reorderData, "Reorder.csv");
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

}
