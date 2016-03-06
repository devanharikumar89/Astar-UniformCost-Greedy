import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchUSA {
	static Map<String, City> cityMap = new HashMap<String, City>();
	static City startingPoint = null;
	static City endingPoint = null;
	static Set<City> expandedCities = new LinkedHashSet<City>();

	public static void main(String[] args) {
		String searchType = args[0];
		String startCity = args[1];
		String destCity = args[2];

		BufferedReader br;
		try {
			URL path = ClassLoader.getSystemResource("usroads.pl");
			if (path == null) {
				System.out.println("FILE usroads.pl NOT FOUND. MAKE SURE IT IS IN THE SAME DIRECTORY");
			}
			File f = new File(path.toURI());
			br = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("city")) {
					Pattern pat = Pattern.compile("\\(([^)]+)\\)");
					Matcher mat = pat.matcher(line);
					if (mat.find()) {
						String lineDetails = mat.group();
						lineDetails = lineDetails.substring(1,
								lineDetails.length() - 1);
						// System.out.println(lineDetails);
						String[] details = lineDetails.split(",");
						cityMap.put(
								details[0].trim(),
								new City(details[0].trim(), Double
										.valueOf(details[1].trim()), Double
										.valueOf(details[2].trim())));
					}
				} else if (line.startsWith("road")) {
					Pattern pat = Pattern.compile("\\(([^)]+)\\)");
					Matcher mat = pat.matcher(line);
					if (mat.find()) {
						String lineDetails = mat.group();
						lineDetails = lineDetails.substring(1,
								lineDetails.length() - 1);
						// System.out.println(lineDetails);
						String[] details = lineDetails.split(",");
						City city1 = cityMap.get(details[0].trim());
						City city2 = cityMap.get(details[1].trim());
						double distance = Double.valueOf(details[2].trim());
						city1.distanceToNeighbors.put(city2, distance);
						city2.distanceToNeighbors.put(city1, distance);
					}

				}
			}
			// System.out.println("number of cities" + cityMap.keySet().size());

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		startingPoint = cityMap.get(startCity);
		endingPoint = cityMap.get(destCity);
		switch (searchType) {
		case "astar":
			aStar(startCity, destCity);
			break;
		case "greedy":
			greedy(startCity, destCity);
			break;
		case "uniformcost":
			uniformCost(startCity, destCity);
			break;
		default:
			System.out.println("UNIDENTIFIED SEARCH TYPE");
		}
	}

	private static void uniformCost(String startCity, String destCity) {
		if (startCity.equals(destCity)) {
			return;
		}
		PriorityQueue<Path> pq = new PriorityQueue<Path>(cityMap.keySet()
				.size(), new Comparator<Path>() {
			@Override
			public int compare(Path o1, Path o2) {
				if (o1.total == o2.total)
					return 0;
				return o1.total - o2.total > 0 ? 1 : -1;
			}
		});
		City start = cityMap.get(startCity);
		Path firstPath = new Path(start);
		firstPath.cost = 0;
		firstPath.heuristic = 0;
		pq.add(firstPath);
		while (!pq.isEmpty()) {
			Path justPolled = pq.poll();
			City lastCity = justPolled.path.getLast();
			// System.out.println("Expanding " + lastCity.name);
			expandedCities.add(lastCity);
			// System.out.println("PQ" + pq.toString());
			if (endingPoint.equals(lastCity)) {
				System.out.println("path : " + justPolled.path.toString());
				System.out.println("Number of cities on path : "
						+ justPolled.path.size());
				System.out.println("expanded : " + expandedCities.toString());
				System.out.println("Number of cities expanded : "
						+ expandedCities.size());
				System.out.println("Path cost : " + justPolled.cost);
				return;
			}
			for (City neighbor : lastCity.distanceToNeighbors.keySet()) {
				if (!expandedCities.contains(neighbor)) {
					Path path = new Path();
					path.path = new LinkedList<City>(justPolled.path);
					path.path.addLast(neighbor);
					path.cost = justPolled.cost
							+ lastCity.distanceToNeighbors.get(neighbor);
					path.total = path.cost;
					pq.offer(path);
				}
			}

		}
	}

	private static void greedy(String startCity, String destCity) {
		if (startCity.equals(destCity)) {
			return;
		}
		PriorityQueue<Path> pq = new PriorityQueue<Path>(cityMap.keySet()
				.size(), new Comparator<Path>() {
			@Override
			public int compare(Path o1, Path o2) {
				if (o1.total == o2.total)
					return 0;
				return o1.total - o2.total > 0 ? 1 : -1;
			}
		});
		City start = cityMap.get(startCity);
		Path firstPath = new Path(start);
		firstPath.cost = 0;
		firstPath.heuristic = 0;
		pq.add(firstPath);
		while (!pq.isEmpty()) {
			Path justPolled = pq.poll();
			City lastCity = justPolled.path.getLast();
			// System.out.println("Expanding " + lastCity.name);
			expandedCities.add(lastCity);
			// System.out.println("PQ" + pq.toString());
			if (endingPoint.equals(lastCity)) {
				System.out.println("path : " + justPolled.path.toString());
				System.out.println("Number of cities on path : "
						+ justPolled.path.size());
				System.out.println("expanded : " + expandedCities.toString());
				System.out.println("Number of cities expanded : "
						+ expandedCities.size());
				System.out.println("Path cost : " + justPolled.cost);
				return;
			}
			for (City neighbor : lastCity.distanceToNeighbors.keySet()) {
				if (!expandedCities.contains(neighbor)) {
					Path path = new Path();
					path.path = new LinkedList<City>(justPolled.path);
					path.path.addLast(neighbor);
					path.cost = justPolled.cost
							+ lastCity.distanceToNeighbors.get(neighbor);
					path.heuristic = heuristic(neighbor);
					path.total = path.heuristic;
					pq.offer(path);
				}
			}

		}
	}

	private static void aStar(String startCity, String destCity) {
		if (startCity.equals(destCity)) {
			return;
		}
		PriorityQueue<Path> pq = new PriorityQueue<Path>(cityMap.keySet()
				.size(), new Comparator<Path>() {
			@Override
			public int compare(Path o1, Path o2) {
				if (o1.total == o2.total)
					return 0;
				return o1.total - o2.total > 0 ? 1 : -1;
			}
		});
		City start = cityMap.get(startCity);
		Path firstPath = new Path(start);
		firstPath.cost = 0;
		firstPath.heuristic = 0;
		pq.add(firstPath);
		while (!pq.isEmpty()) {
			// System.out.println("Datastructure : " + pq.toString());
			Path justPolled = pq.poll();
			City lastCity = justPolled.path.getLast();
			// System.out.println("Expanding " + lastCity.name);
			expandedCities.add(lastCity);
			if (endingPoint.equals(lastCity)) {
				System.out.println("path : " + justPolled.path.toString());
				System.out.println("Number of cities on path : "
						+ justPolled.path.size());
				System.out.println("expanded : " + expandedCities.toString());
				System.out.println("Number of cities expanded : "
						+ expandedCities.size());
				System.out.println("Path cost : " + justPolled.cost);
				return;
			}
			for (City neighbor : lastCity.distanceToNeighbors.keySet()) {
				if (!expandedCities.contains(neighbor)) {
					Path path = new Path();
					path.path = new LinkedList<City>(justPolled.path);
					path.path.addLast(neighbor);
					path.cost = justPolled.cost
							+ lastCity.distanceToNeighbors.get(neighbor);
					path.heuristic = heuristic(neighbor);
					path.total = path.cost + path.heuristic;
					pq.offer(path);
				}
			}

		}
	}

	private static double heuristic(City neighbor) {
		return Math
				.sqrt(Math.pow(
						69.5 * (neighbor.latitude - endingPoint.latitude), 2)
						+ Math.pow(
								69.5
										* Math.cos((neighbor.latitude + endingPoint.latitude)
												/ 360 * Math.PI)
										* (neighbor.longitude - endingPoint.longitude),
								2));
	}
}
