import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collections;
import java.util.Stack;

public class ImageGenerator {

	// private final int SIDE = 100000;
	private final double OCCUPATION_RATE = 0.3;
	private final double DYE_AREA_RATE = 0.1;
	private final double CANCER_PROBABILITY = 0.001;
	private final int NEIGHBOR_NUM = 4;
	private final double CENTER_RATE = 0.3;
	private final double EDGE_RATE = 0.4;

	private int SIDE;
	private int[][] offset = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};

	public ImageGenerator() {

	}

	public ImageGenerator(int side) {
		SIDE = side;
	}

	private String toStr(int[] point) {
		return Integer.toString(point[0]) + "," + Integer.toString(point[1]);
	}

	private boolean isInRange(int[] point) {
		return (point[0] >= 0 && point[0] < SIDE && point[1] >= 0 && point[1] < SIDE);
	}
	
	private boolean isOnTheEdge(int[] point) {
		return (point[0] <= 0 || point[0] >= SIDE - 1 || point[1] <= 0 || point[1] >= SIDE - 1);
	}

	public ArrayList<int[]> MicroscopeImageGenerator() {

		ArrayList<int[]> contourList = new ArrayList<>();
		HashSet<String> contourSet = new HashSet<>();
		HashSet<String> visitedSet = new HashSet<>();
		ArrayList<int[]> visitedList = new ArrayList<>();

		// randomly pick a start point
		// CENTER_RATE and EDGE_RATE makes the point located in the center area approximately
		int randX = (int) (Math.random() * SIDE * CENTER_RATE) + (int) (SIDE * EDGE_RATE);
		int randY = (int) (Math.random() * SIDE * CENTER_RATE) + (int) (SIDE * EDGE_RATE);
		int[] startP = new int[] {randX, randY};
		contourList.add(startP);
		contourSet.add(toStr(startP));
		visitedSet.add(toStr(startP));
		visitedList.add(startP);

		while (true) {

			// if the area reaches the goal, break
			if (visitedSet.size() >= OCCUPATION_RATE * SIDE * SIDE) break;

			// randomly pick a point in contour list
			int randIndex = (int) (Math.random() * contourList.size());
			int[] currPoint = contourList.get(randIndex);

			// expand the contour
			int numExpanded = 0;
			for (int i = 0; i < NEIGHBOR_NUM; i++) {
				int[] newPoint = new int[] {currPoint[0] + offset[i][0], currPoint[1] + offset[i][1]};
				if (!isInRange(newPoint)) continue;
				if (!visitedSet.contains(toStr(newPoint))) {
					contourList.add(newPoint);
					contourSet.add(toStr(newPoint));
					visitedSet.add(toStr(newPoint));
					visitedList.add(newPoint);
					numExpanded++;
				}
			}
			if (numExpanded > 0) {
				contourSet.remove(toStr(currPoint));
				contourList.remove(randIndex);
			}
		}
		return visitedList;
	}
	
	public ArrayList<int[]> DyeSensorImageGenerator() {

		ArrayList<int[]> visitedList = new ArrayList<>();
		HashSet<String> visitedSet = new HashSet<>();
		Stack<int[]> stack = new Stack<>();

		double rateOfArea = DYE_AREA_RATE * OCCUPATION_RATE * (Math.random() <= CANCER_PROBABILITY ? 1.2 : 0.8);
		
		int randX = (int) (Math.random() * SIDE * CENTER_RATE) + (int) (SIDE * EDGE_RATE);
		int randY = (int) (Math.random() * SIDE * CENTER_RATE) + (int) (SIDE * EDGE_RATE);
		int[] startP = new int[] {randX, randY};
		stack.push(startP);

		/*
		// DFS (flood fill): stack implementation
		// to generate a "thinner" area
		*/
		int[] currPoint, newPoint, prevPoint = startP;
		double temperature = 0.8;
		while (!stack.isEmpty()) {

			if (visitedSet.size() >= rateOfArea * SIDE * SIDE) break;

			currPoint = stack.pop();
			visitedList.add(currPoint);
			visitedSet.add(toStr(currPoint));

			/*
			// Simulated Annealing (introduce probability)
			// expected that at the beginning, it can expand more quickly (away from previos point)
			*/
			if (Math.random() < temperature && visitedSet.size() > 1) {
				int[] delta = new int[] {currPoint[0] - prevPoint[0], currPoint[1] - prevPoint[1]};
				int index = delta[0] != 0 ? (delta[0] > 0 ? 2 : 3) : (delta[1] > 0 ? 1 : 0);
				newPoint = new int[] {currPoint[0] + offset[index][0], currPoint[1] + offset[index][1]};
			}
			temperature -= 2.0 / (SIDE * SIDE);

			/*
			// generate an array of direction with random order
			// in order to randomly pick the expanding the direction
			*/
			int[] randArr = new int[NEIGHBOR_NUM];
			int m = 0, n;
			HashSet<Integer> randomSet = new HashSet<>();
			while (randomSet.size() < NEIGHBOR_NUM) {
				if (randomSet.add(n = (int) (Math.random() * NEIGHBOR_NUM))) randArr[m++] = n;
			}

			// pop the top and push the neighbor to the top
			for (int i = 0; i < randArr.length; i++) {
				newPoint = new int[] {currPoint[0] + offset[randArr[i]][0], currPoint[1] + offset[randArr[i]][1]};
				if (!isInRange(newPoint)) continue;
				if (visitedSet.contains(toStr(newPoint))) continue;
				visitedSet.add(toStr(newPoint));
				visitedList.add(newPoint);
				stack.push(newPoint);
			}
		}
		return visitedList;
	}

	private void printPoint(int[] p) {
		System.out.println(p[0] + "," + p[1]);
	}

	public ArrayList<int[]> generate() {
		// return MicroscopeImageGenerator();
		return DyeSensorImageGenerator();
	}
 }
