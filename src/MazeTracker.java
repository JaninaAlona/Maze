import java.util.*;
public class MazeTracker {
	private char[][] mazeToTrack;
	private char[][] mazeWithWay;
	private StringBuffer protocol;
	private int roads;
	private boolean invalid;
	private ArrayList<Integer> way;
	private ArrayList<Character> wayElements;
	private ArrayList<String> wayDirections;
	ArrayList<Integer> toRemove;
	public MazeTracker(char[][] mazeToTrack) {
		this.mazeToTrack = mazeToTrack;
		mazeWithWay = new char[mazeToTrack.length][mazeToTrack.length];
		for(int i = 0; i < mazeToTrack.length; i++) {
			for(int j = 0; j < mazeToTrack[i].length; j++) {
				mazeWithWay[i][j] = mazeToTrack[i][j];
			}
		}
		way = new ArrayList<Integer>();
		wayElements = new ArrayList<Character>();
		wayDirections = new ArrayList<String>();
		toRemove = new ArrayList<Integer>();
		roads = 1;
	}
	public void searchWay() {
		protocol = new StringBuffer();
		int startRow = 0;
		int startCol = 0;
		//Find start coordinates.
		for(int row = 0; row < mazeWithWay.length; row++) {
			for(int col = 0; col < mazeWithWay[row].length; col++) {
				if(mazeWithWay[row][col] == 'b') {
					startRow = row;
					startCol = col;
				}
				//Count possible road elements.
				if((mazeWithWay[row][col] == ' ') || (mazeWithWay[row][col] == 'z')) {
					roads++;
				}
			}
		}
		traverse(startRow, startCol);
		//Fill protocol with directions.
		for(int i = 0; i < wayDirections.size(); i++) {
			protocol.append(wayDirections.get(i));
		}
	}
	//Throw an exception if the way does not contain a finish point.
	public void examineWay() throws InvalidMazeException {
		if(!wayElements.contains('z')) {
			invalid = true;
			throw new InvalidMazeException();
		}
	}
	public void traverse(int row, int col) {
		int roadCounter = 0;
		int currentRow = row;
		int currentCol = col;
		int cross = 0;
		boolean finished = false;
		boolean contains = false;
		ArrayList<Integer> crossCoordinates = new ArrayList<Integer>();
		String[] nextCoordinates = new String[3];
		do { 
			//Get the current way element.
			char currentWay = mazeWithWay[currentRow][currentCol];
			if(currentWay == 'b') {
				way.add(currentRow);
				way.add(currentCol);
				wayElements.add(currentWay);
				wayDirections.add("Entering the maze.\n");
			} else if(currentWay == ' ') {
				mazeWithWay[currentRow][currentCol] = '*';
				way.add(currentRow);
				way.add(currentCol);
				wayElements.add(mazeWithWay[currentRow][currentCol]);
			} else if(currentWay == 'z') {
				way.add(currentRow);
				way.add(currentCol);
				wayElements.add(currentWay);
				wayDirections.add("Finish.\n\n");
				finished = true;
			}
			//Save all directions (north, east, south, west) in a LinkedHashMap with string coordinates as keys
			//character way elements as values
			LinkedHashMap<String, Character> directions = searchDirection(currentRow, currentCol);
			if(currentWay != 'b') {
				//Count path ways.
				for(String i : directions.keySet()) {
					if((directions.get(i) == ' ') || (directions.get(i) == 'z') || (directions.get(i) == '*') || (directions.get(i) == 'b') ) {
						cross++;
					}
				}
			} else {
				cross = 2;
			}
			//If the way element is a crossroad, only add it to the ArrayList if it is not already contained
			if(cross > 2) {
				if(crossCoordinates.isEmpty()) {
					crossCoordinates.add(currentRow);
					crossCoordinates.add(currentCol);
				} else {
					for(int i = 0; i < crossCoordinates.size() - 1; i = i + 2) {
						int rowInside = crossCoordinates.get(i);
						int colInside = crossCoordinates.get(i+1);
						if((rowInside == currentRow) && (colInside == currentCol)) {
							contains = true;
						}
					}
					if(!contains) {
						crossCoordinates.add(currentRow);
						crossCoordinates.add(currentCol);
					}
				}
			}
			for(String i : directions.keySet()) {
				if(((directions.get(i) == ' ') || (directions.get(i) == 'z')) && (cross > 1)) {
					//Split information in string in LinkedHashMap
					nextCoordinates = extractCoordinates(i);
					String currentRowS = nextCoordinates[0];
					String currentColS = nextCoordinates[1];
					String direction = nextCoordinates[2];
					//Convert String to Integer and step forward in labyrinth.
					currentRow = Integer.parseInt(currentRowS);
					currentCol = Integer.parseInt(currentColS);
					if(directions.get(i) != 'z') {
						wayDirections.add(direction+"\n");
					}
				} 
				//If we are in a dead end get the last crossroad coordinates from list.
				if((directions.get(i) == 's') && (cross == 1)) {
					int lastCrossRow = crossCoordinates.get(crossCoordinates.size()-2);
					int lastCrossCol = crossCoordinates.get(crossCoordinates.size()-1);
					int indexCol = 0;
					//Find crossroad coordinates in way coordinates list.
					for(int j = 0; j < way.size() - 1; j = j + 2) {
						int rowInside = way.get(j);
						int colInside = way.get(j+1);
						if((rowInside == lastCrossRow) && (colInside == lastCrossCol)) {
							indexCol = j+1;
						}
					}
					//Save removal index and add Coordinates to remove to a list. 
					int removalIndex = indexCol+1;
					for(int j = removalIndex; j < way.size(); j++) {
						toRemove.add(way.get(j));
					}
					//Remove all coordinates of deadend way.
					for(int j = way.size()-1; j >= removalIndex; j--) {
						way.remove(j);
					}
					//Remove all way char elements.
					int removalIndexChar = removalIndex / 2;
					for(int j = wayElements.size()-1; j >= removalIndexChar; j--) {
						wayElements.remove(j);	
					}
					for(int j = wayDirections.size()-1; j >= removalIndexChar; j--) {
						wayDirections.remove(j);
					}
					//Jump back to last crossroad.
					currentRow = lastCrossRow;
					currentCol = lastCrossCol;
				}
			}
			cross = 0;
			roadCounter++;
		} while((roadCounter <= roads) && (!finished));
		//Remove deadend way elements from 2D maze array.
		for(int j = 0; j < toRemove.size()-1; j++) {
			int removeRow = toRemove.get(j);
			int removeCol = toRemove.get(j+1);
			mazeWithWay[removeRow][removeCol] = ' ';
		}
	}
	//Converts coordinate key String in LinkedHashMap to 3 single strings.
	public String[] extractCoordinates(String coordinates) {
		String[] nextCoordinates = new String[3];
		String[] splitted = coordinates.split("_");
		String sRow = splitted[0];
		String sCol = splitted[1];
		String direction = splitted[2];
		nextCoordinates[0] = sRow;
		nextCoordinates[1] = sCol;
		nextCoordinates[2] = direction;
		return nextCoordinates;
	}
	//Saves every direction of a way element. If there is no direction it saves -1_-1_<direction>, x
	public LinkedHashMap<String, Character> searchDirection(int row, int col) {
		LinkedHashMap<String, Character> directions = new LinkedHashMap<String, Character>();
		int northRow = row - 1;
		int northCol = col;
		if(northRow >= 0) {
			char north = mazeWithWay[northRow][northCol];
			String northCoordinates = String.valueOf(northRow)+"_"+String.valueOf(northCol)+"_north";
			directions.put(northCoordinates, north);
		} else {
			directions.put("-1_-1_n", 'x');
		}
		int eastRow = row;
		int eastCol = col + 1;
		if(eastCol < mazeWithWay.length) {
			char east = mazeWithWay[eastRow][eastCol];
			String eastCoordinates = String.valueOf(eastRow)+"_"+String.valueOf(eastCol)+"_east";
			directions.put(eastCoordinates, east);
		} else {
			directions.put("-1_-1_e", 'x');
		}
		int southRow = row + 1;
		int southCol = col;
		if(southRow < mazeWithWay.length) {
			char south = mazeWithWay[southRow][southCol];
			String southCoordinates = String.valueOf(southRow)+"_"+String.valueOf(southCol)+"_south";
			directions.put(southCoordinates, south);
		} else {
			directions.put("-1_-1_s", 'x');
		}
		int westRow = row;
		int westCol = col - 1;
		if(westCol >= 0) {
			char west = mazeWithWay[westRow][westCol];
			String westCoordinates = String.valueOf(westRow)+"_"+String.valueOf(westCol)+"_west";
			directions.put(westCoordinates, west);
		} else {
			directions.put("-1_-1_w", 'x');
		}
		return directions;
	}
	public StringBuffer getWay() {
		return protocol;
	}
	public char[][] getMaze() {
		return mazeWithWay;
	}
	public boolean getInvalid() {
		return invalid;
	}
	public int getDistance() {
		return wayElements.size();
	}
}
