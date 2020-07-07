import java.util.*;
public class MazeParser {
	private String mazeDescription;
	private String maze;
	private int wallThickness;
	private ArrayList<Character> wall;
	private char[][] twoDMaze;
	private boolean invalid;
	MazeParser(String mazeDescription) {
		this.mazeDescription = mazeDescription;
		invalid = false;
	}
	public void removePrefix(){
		String[] help = mazeDescription.split("=");
		maze = help[1];
	}
	//Exception is throw if maze is not quadratic.
	public void testSqrt() throws InvalidMazeException {
		double sqrtResult = Math.sqrt(maze.length());
		if((sqrtResult % 2.0 == 1.0) || (sqrtResult % 2.0 == 0.0)) {
			wallThickness = (int) sqrtResult;
		} else {
			invalid = true;
			throw new InvalidMazeException();
		}
	}
	public void examineFinish() throws InvalidMazeException {
		if(!maze.contains("z")) {
			invalid = true;
			throw new InvalidMazeException();
		}
	}	
	public void constructWalls() {
		wall = new ArrayList<Character>();
		for(int row = 0; row < wallThickness; row++) {
			for(int col = 0; col < wallThickness; col++) {
				//Get 1D location of 2D maze array.
				int loc = col + row * wallThickness;
				if((row == 0) && (col < wallThickness)) {
					wall.add(maze.charAt(loc));
				}
				if(((row > 0) && (row < wallThickness-1)) && ((col == 0) || (col == wallThickness-1))) {
					wall.add(maze.charAt(loc));
				}
				if((row == wallThickness-1) && (col < wallThickness)) {
					wall.add(maze.charAt(loc));
				}
			}
		}
	}
	public void examineHoles() throws InvalidMazeException {
		for(int i = 0; i < wall.size(); i++) {
			if(wall.contains('k')) {
				invalid = true;
				throw new InvalidMazeException();
			}
		}
	}
	public void examineStart() throws InvalidMazeException {
		for(int i = 0; i < wall.size(); i++) {
			if(!wall.contains('b')) {
				invalid = true;
				throw new InvalidMazeException();
			}
		}
	}
	public void replaceChars() {
		maze = maze.replace('w', '#');
		maze = maze.replace('k', ' ');
	}
	//Construct 2D maze.
	public void alignMaze() {
		twoDMaze = new char[wallThickness][wallThickness];
		for(int i = 0; i < wallThickness; i++) {
			for(int j = 0; j < wallThickness; j++) {
				twoDMaze[i][j] = maze.charAt(i+(j*wallThickness));
			}
		}
	}
	public char[][] getMaze() {
		return twoDMaze;
	}
	public String getMazeDescription() {
		return mazeDescription;
	}
	public boolean getInvalid() {
		return invalid;
	}
}
