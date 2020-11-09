import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Stack;

public class ImageGenerator {

	// private final int SIDE = 100000;
	private final double OCCUPATION_RATE = 0.25;
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

	private int[] toArr(String s) {
		String[] ss = s.split(",");
		return new int[] {Integer.parseInt(ss[0]), Integer.parseInt(ss[1])};
	}

	private boolean isInRange(int[] point) {
		return (point[0] >= 0 && point[0] < SIDE && point[1] >= 0 && point[1] < SIDE);
	}

	private boolean isOnTheEdge(int[] point) {
		return (point[0] == 0 || point[0] == SIDE - 1 || point[1] == 0 || point[1] == SIDE - 1);
	}

	private int expandContour(int[] point, HashSet<String> contourSet, ArrayList<int[]> contourList, HashSet<String> innerSet) {
		int numExpanded = 0;
		for (int i = 0; i < NEIGHBOR_NUM; i++) {
			int[] newPoint = new int[] {point[0] + offset[i][0], point[1] + offset[i][1]};
			if (!isInRange(newPoint)) continue;
			if (!contourSet.contains(toStr(newPoint)) && !innerSet.contains(toStr(newPoint))) {
				contourList.add(newPoint);
				contourSet.add(toStr(newPoint));
				numExpanded++;
			}
		}
		return numExpanded;
	}

	private void expandInnerContour(int[] point, HashSet<String> innerSet) {
		innerSet.add(toStr(point));
	}

	private boolean toRemoveContour(int[] point, HashSet<String> contourSet, HashSet<String> innerSet) {
		if (isOnTheEdge(point)) return false;
		for (int i = 0; i < NEIGHBOR_NUM; i++) {
			int[] newPoint = new int[] {point[0] + offset[i][0], point[1] + offset[i][1]};
			if (!isInRange(newPoint)) continue;
			if (!contourSet.contains(toStr(newPoint)) && !innerSet.contains(toStr(newPoint))) {
				return false;
			}
		}
		return true;
	}

	private boolean toRemoveInner(String s, HashSet<String> contourSet, HashSet<String> innerSet) {
		int count = 0;
		for (int i = 0; i < NEIGHBOR_NUM; i++) {
			int[] point = toArr(s);
			int[] newPoint = new int[] {point[0] + offset[i][0], point[1] + offset[i][1]};
			if (!isInRange(newPoint)) continue;
			if (contourSet.contains(toStr(newPoint))) return false;
			if (innerSet.contains(toStr(newPoint))) count++;
		}
		if (count >= 1) return true;
		return false;
	}

	private int updateContour(int[] point, int index, HashSet<String> contourSet, ArrayList<int[]> contourList, HashSet<String> innerSet, int count) {
		
		// some points are not expandable, so remove from contourList, add it to innerContourList
		for (int i = 0; i < contourList.size(); i++) {
			int[] p = contourList.get(i);
			if (toRemoveContour(p, contourSet, innerSet)) {
				contourList.remove(i--);
				contourSet.remove(toStr(p));
				expandInnerContour(p, innerSet);
			}
		}

		// some points are "really inside" -- inside the innerContour, remove them
		int numCount = count;
		Iterator<String> iter = innerSet.iterator();
		while (iter.hasNext()) {
			String p = iter.next();
			if (toRemoveInner(p, contourSet, innerSet)) {
				iter.remove();
				numCount++;
			}
		}
		return numCount;
	}

	public ArrayList<int[]> MicroscopeImageGenerator() {

		ArrayList<int[]> contourList = new ArrayList<>();
		HashSet<String> contourSet = new HashSet<>();
		HashSet<String> innerSet = new HashSet<>();

		// randomly pick a start point
		// CENTER_RATE and EDGE_RATE makes the point located in center area approximately
		int randX = (int) (Math.random() * SIDE * CENTER_RATE) + (int) (SIDE * EDGE_RATE);
		int randY = (int) (Math.random() * SIDE * CENTER_RATE) + (int) (SIDE * EDGE_RATE);
		int[] startP = new int[] {randX, randY};
		expandContour(startP, contourSet, contourList, innerSet);
		expandInnerContour(startP, innerSet);

		int count = 0;
		int size = 0;
		while (true) {

			// pick random point in contour list
			int randIndex = (int) (Math.random() * contourSet.size());
			int[] currPoint = contourList.get(randIndex);
			if (!contourSet.contains(toStr(currPoint))) continue;

			/* expand and update
			// add neighbors to contourList and remove currPoint from contourList
			// add currPoint to innerContourList and update 
			// more details in the doc
			*/
			int numExpanded = expandContour(currPoint, contourSet, contourList, innerSet);
			if (numExpanded > 0) {
				size = updateContour(currPoint, randIndex, contourSet, contourList, innerSet, size);
			}
			
			if (size + contourSet.size() >= OCCUPATION_RATE * SIDE * SIDE) break;
		}
		return contourList;
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
			temperature -= 3.0 / (SIDE * SIDE);

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

 }