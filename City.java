import java.util.HashMap;
import java.util.Map;

public class City {
	String name;
	double latitude;
	double longitude;
	Map<City, Double> distanceToNeighbors;

	City(String name, double latitude, double longitude) {
		distanceToNeighbors = new HashMap<City, Double>();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return name;
	}
}
