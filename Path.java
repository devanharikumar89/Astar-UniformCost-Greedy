import java.util.LinkedList;

public class Path {
	LinkedList<City> path;
	double cost;
	double heuristic;
	double total;

	public Path() {
		path = new LinkedList<City>();
	}
	public Path(City start) {
		path = new LinkedList<City>();
		path.addLast(start);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return path.toString();
	}
}
