
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

public class CancerChecker {

	private int SIDE;

	public CancerChecker() {

	}

	public CancerChecker(int side) {
		SIDE = side;
	}

	private int rowOf(int[] point) {
		return point[0];
	}

	public int[] check(ArrayList<int[]> microscopeImage, ArrayList<int[]> dyeSensorImage) {

		HashMap<Integer, HashSet<Integer>> microscopeImageMap = new HashMap<>();
		HashMap<Integer, ArrayList<int[]>> dyeSensorImageMap = new HashMap<>();

		// since I use the list of points as input instead of image, so this part is scan by row
		for (int[] point : microscopeImage) {
			HashSet<Integer> pixelList = new HashSet<>();
			if (microscopeImageMap.containsKey(rowOf(point))) {
				pixelList = microscopeImageMap.get(rowOf(point));
			}
			pixelList.add(point[1]);
			microscopeImageMap.put(rowOf(point), pixelList);
		}
		for (int[] point : dyeSensorImage) {
			ArrayList<int[]> pixelList = new ArrayList<>();
			if (dyeSensorImageMap.containsKey(rowOf(point))) {
				pixelList = dyeSensorImageMap.get(rowOf(point));
			}
			pixelList.add(point);
			dyeSensorImageMap.put(rowOf(point), pixelList);
		}

		// calculate the rate
		int total = 0;
		int area = 0;
		for (int row = 0; row < SIDE; row++) {
			int count = 0;
			if (!microscopeImageMap.containsKey(row)) continue;
			HashSet<Integer> microscopeSet = microscopeImageMap.get(row);
			area += microscopeSet.size();
			if (!dyeSensorImageMap.containsKey(row)) continue;
			ArrayList<int[]> dyeSensorSet = dyeSensorImageMap.get(row);
			for (int[] point : dyeSensorSet) {
				if (microscopeSet.contains(point[1])) count++;
			}
			total += count;
		}

		return new int[] {total, area};
	}
}
