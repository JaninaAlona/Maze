//Labyrinth 3. Versuch bei Programmier Praktikum
import java.io.*;
import java.util.*;
public class MazeTest {
	public static void main(String[] args) {
		File textFile = new File("Labyrinthe.txt");
		String fileLine = new String();
		ArrayList<MazeParser> inputMazes = new ArrayList<MazeParser>();
		try {
			//Read text file and create MazeParser objects from one file line.
			BufferedReader br = new BufferedReader(new FileReader(textFile));
			while((fileLine = br.readLine()) != null) {
				MazeParser mazeParser = new MazeParser(fileLine);
				inputMazes.add(mazeParser);
			}
			br.close();
		} catch(FileNotFoundException fnfe) {
			System.out.println("File not found.");
		} catch(IOException ioe) {
			System.out.println("File error.");
		}
		try {
			//Create a new file.
			BufferedWriter bw = new BufferedWriter(new FileWriter("Landkarte.txt"));
			for(int i = 0; i < inputMazes.size(); i++) {
				MazeParser currentParser = inputMazes.get(i);
				currentParser.removePrefix();
				try {
					currentParser.testSqrt();
				} catch(InvalidMazeException ime) {
					bw.write(currentParser.getMazeDescription());
					bw.write("\n");
					bw.write("Maze is not quadratic.");
					bw.write("\n\n");
				}
				try {
					currentParser.examineFinish();
				} catch(InvalidMazeException ime) {
					bw.write(currentParser.getMazeDescription());
					bw.write("\n");
					bw.write("Maze has no finish point.");
					bw.write("\n\n");
				}
				currentParser.constructWalls();
				try {
					currentParser.examineHoles();
				} catch(InvalidMazeException ime) {
					bw.write(currentParser.getMazeDescription());
					bw.write("\n");
					bw.write("Maze has a hole in the wall.");
					bw.write("\n\n");
				}
				try {
					currentParser.examineStart();
				} catch(InvalidMazeException ime) {
					bw.write(currentParser.getMazeDescription());
					bw.write("\n");
					bw.write("Maze has no start point.");
					bw.write("\n\n");
				}
				if(!currentParser.getInvalid()) {
					currentParser.replaceChars();
					currentParser.alignMaze();
					char[][] parsedMaze = currentParser.getMaze();
					MazeTracker tracker = new MazeTracker(parsedMaze);
					tracker.searchWay();
					try {
						tracker.examineWay();
					} catch(InvalidMazeException ime) {
						bw.write(currentParser.getMazeDescription());
						bw.write("\n");
						bw.write("Finish cannot be reached.");
						bw.write("\n\n");
					}
					if(!tracker.getInvalid()) {
						//Write to output maze, directions and distance to the output file.
						bw.write(currentParser.getMazeDescription());
						bw.write("\n");
						char[][] mazeWithWay = tracker.getMaze();
						for(int k = 0; k < mazeWithWay.length; k++) {
							for(int j = 0; j < mazeWithWay[k].length; j++) {
								bw.write(mazeWithWay[k][j]+" ");
							}
							bw.write("\n");
						}
						bw.write("\n");
						StringBuffer protocol = tracker.getWay();
						int distance = tracker.getDistance();
						bw.write("Distance to finish "+distance+"\n");
						String stringProtocol = protocol.toString();
						bw.write(stringProtocol);
					}
				}
			}
			bw.close();
		} catch(IOException ioe) {
			System.out.println("File error.");
		}
	}
} 
