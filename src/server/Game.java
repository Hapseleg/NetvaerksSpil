package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Game {
	/*
	 * Protokoller: LCasper (sendes når en leaver) JCasper (sendes når en joiner
	 * til dem der allerede er i spillet) CCasper,Bob,Mike (Sendes til en der
	 * lige er joinet) U17,6,U,4#13,8,D,0 (sendes når nogen går eller point
	 * skifter) Txx,yy (treasure spawner) Sxx,yy,xx,yy,d (laser skud, xy start
	 * og xy slut og direction)
	 */

	private String board;
	private String[][] maze;
	private ArrayList<PlayerThread> players;
	private Random rand;
	private int size, visitedCell, floor;
	private Treasure currentTreasure;
	private boolean[][] visited;

	public Game() {
		this.players = new ArrayList<>();
		this.board = "";
		rand = new Random();
		this.size = 20;
		createBoardArray(size);
	}

	private void createBoardArray(int size) {

		maze = genRandMaze(size);
		checkClosed(maze);
		while (visitedCell != floor) {
			maze = genRandMaze(size);
			checkClosed(maze);
		}

		for (String[] s : maze) {
			for (String st : s) {
				board += st;
			}
		}
	}

	public void addPlayer(PlayerThread player) {
		try {
			this.players.add(player);
			player.sendMessage(board.toString());
			addPlayerToBoard(player);

			if (players.size() >= 2) {
				spawnTreasure();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void addPlayerToBoard(PlayerThread player) {
		boolean validPos = false;
		int x = 0, y = 0;

		while (!validPos) {
			x = rand.nextInt(size - 1) + 1;
			y = rand.nextInt(size - 1) + 1;

			if (!maze[y][x].equals("w")) {
				boolean playerFoundAtPos = false;
				int i = 0;
				while (!playerFoundAtPos && i < players.size()) {
					if (players.get(i).getXpos() == x && players.get(i).getYpos() == y) {
						playerFoundAtPos = true;
					}
					i++;
				}
				validPos = !playerFoundAtPos;
			}
		}
		player.setXpos(x);
		player.setYpos(y);
	}

	private synchronized void spawnTreasure() {
		boolean validPos = false;
		int x = 0, y = 0, points = rand.nextInt(50000) + 10000;// TODO points
																// for treasure
		while (!validPos) {
			x = rand.nextInt(size - 1) + 1;
			y = rand.nextInt(size - 1) + 1;

			if (!maze[y][x].equals("w")) {
				boolean objectFoundAtPos = false;
				int i = 0;
				while (!objectFoundAtPos && i < players.size()) {
					if (players.get(i).getXpos() == x && players.get(i).getYpos() == y) {
						objectFoundAtPos = true;
					}
					i++;
				}
				validPos = !objectFoundAtPos;
			}
		}
		currentTreasure = new Treasure(x, y, points);
		notifyAllPlayers("T" + x + "," + y);
	}

	private synchronized void shotFired(PlayerThread player) {
		int x = player.getXpos(), y = player.getYpos();
		String d = player.getDirection();
		int[] lastXY = new int[2];
		boolean validShot = !checkForTreasure(x, y) && !checkForWall(x, y) && checkForPlayer(x, y) == null;

		switch (d) {
		case "U": {
			y = y - 1;
			validShot = !checkForTreasure(x, y) && !checkForWall(x, y) && checkForPlayer(x, y) == null;
			if (validShot) {
				lastXY = calculateShotFired(x, y, d, player);
			}
			break;
		}
		case "D": {
			y = y + 1;
			validShot = !checkForTreasure(x, y) && !checkForWall(x, y) && checkForPlayer(x, y) == null;
			if (validShot) {
				lastXY = calculateShotFired(x, y, d, player);
			}
			break;
		}
		case "L": {
			x = x - 1;
			validShot = !checkForTreasure(x, y) && !checkForWall(x, y) && checkForPlayer(x, y) == null;
			if (validShot) {
				lastXY = calculateShotFired(x, y, d, player);
			}
			break;
		}
		case "R": {
			x = x + 1;
			validShot = !checkForTreasure(x, y) && !checkForWall(x, y) && checkForPlayer(x, y) == null;
			if (validShot) {
				lastXY = calculateShotFired(x, y, d, player);
			}
			break;
		}
		default:
			System.out.println("default game.shotFired");
			break;
		}
		String s = "S" + x + "," + y + "," + lastXY[0] + "," + lastXY[1] + "," + d;

		if (validShot) {
			notifyAllPlayers(s);
			updatePositions();
		}
	}

	private synchronized int[] calculateShotFired(int x, int y, String direction, PlayerThread player) {
		int[] lastXY = { x, y };

		// checker om den næste position er en væg eller en spiller, hvis der
		// ikke er kaldes calculateShotFired igen
		// hvis der ikke er, returneres den nuværende x,y
		PlayerThread foundPlayer = null;
		switch (direction) {
		case "U": {
			foundPlayer = checkForPlayer(x, y - 1);
			if (foundPlayer == null && !checkForWall(x, y - 1) && !checkForTreasure(x, y - 1)) {
				lastXY = calculateShotFired(x, y - 1, direction, player);
			}
			break;
		}
		case "D": {
			foundPlayer = checkForPlayer(x, y + 1);
			if (foundPlayer == null && !checkForWall(x, y + 1) && !checkForTreasure(x, y + 1)) {
				lastXY = calculateShotFired(x, y + 1, direction, player);
			}
			break;
		}
		case "L": {
			foundPlayer = checkForPlayer(x - 1, y);
			if (foundPlayer == null && !checkForWall(x - 1, y) && !checkForTreasure(x - 1, y)) {
				lastXY = calculateShotFired(x - 1, y, direction, player);
			}
			break;
		}
		case "R": {
			foundPlayer = checkForPlayer(x + 1, y);
			if (foundPlayer == null && !checkForWall(x + 1, y) && !checkForTreasure(x + 1, y)) {
				lastXY = calculateShotFired(x + 1, y, direction, player);
			}
			break;
		}
		default:
			System.out.println("default game.calculateShotFired");
			break;
		}

		if (foundPlayer != null) {
			int points = foundPlayer.getPoint() / 3;
			if (points >= 0) {
				foundPlayer.reducePoints(points);
				player.increasePoints(points);
			}
		}
		return lastXY;
	}

	/**
	 *
	 * @param message
	 * @param player
	 * @throws IOException
	 * @throws Exception
	 */
	public synchronized void receiveMessage(String message, PlayerThread player) throws IOException {
		int x = player.getXpos();
		int y = player.getYpos();
		switch (message.charAt(0)) {
		case 'U': {// Up
			movePlayer(x, y - 1, "U", player);
			break;
		}
		case 'D': {// Down
			movePlayer(x, y + 1, "D", player);
			break;
		}
		case 'L': {// Left
			movePlayer(x - 1, y, "L", player);
			break;
		}
		case 'R': {// Right
			movePlayer(x + 1, y, "R", player);
			break;
		}
		case 'N': {
			boolean nameTaken = false;
			for (PlayerThread p : players) {
				if (p.getPlayerName().equals(message.substring(1))) {
					nameTaken = true;
				}
			}
			if (nameTaken) {
				player.sendMessage("0");
			} else {
				player.sendMessage("1");
				player.setPlayerName(message.substring(1));
				addPlayer(player);

				String c = "C";
				for (PlayerThread p : players) {
					if (!p.equals(player)) {
						c += p.getPlayerName() + ",";
					}
				}
				player.sendMessage(c + player.getPlayerName());

				// send navnene på de spillere der allerede er i spillet
				for (PlayerThread p : players) {
					if (!p.equals(player)) {
						p.sendMessage("J" + player.getPlayerName());
					}
				}
				updatePositions();
			}
			break;
		}
		case 'X': {
			players.remove(player);
			player.setKeepRunning(false);
			break;
		}
		case 'S': {
			shotFired(player);
			break;
		}
		default: {
			System.out.println("Default i game.receiveMessage");
		}
		}
	}

	private void movePlayer(int x, int y, String direction, PlayerThread player) throws IOException {
		PlayerThread foundPlayer = checkForPlayer(x, y);

		if (maze[y][x].equals("w")) {
			player.reducePoints(1);
		} else if (foundPlayer != null) {
			foundPlayer.reducePoints(50);
			player.increasePoints(50);
		} else {
			player.setYpos(y);
			player.setXpos(x);
			player.increasePoints(1);

			if (checkForTreasure(x, y)) {
				player.increasePoints(currentTreasure.getPoint());
				// TODO skal der sendes et T for at slette den nuværende
				// treasure client side?
				if (players.size() >= 2) {
					spawnTreasure();
				}
			}
		}
		player.setDirection(direction);

		// U17,6,U,4#13,8,D,0
		updatePositions();
	}

	public void removePlayer(PlayerThread player) {
		players.remove(player);
		notifyAllPlayers("L" + player.getName());
		updatePositions();
	}

	private PlayerThread checkForPlayer(int x, int y) {
		PlayerThread foundPlayer = null;

		for (PlayerThread p : players) {
			if (p.getXpos() == x && p.getYpos() == y) {
				foundPlayer = p;
				break;
			}
		}
		return foundPlayer;
	}

	private boolean checkForWall(int x, int y) {
		return maze[y][x].equals("w");

	}

	private boolean checkForTreasure(int x, int y) {
		boolean foundTreasure = false;

		if (currentTreasure != null) {
			if (currentTreasure.getX() == x && currentTreasure.getY() == y) {
				foundTreasure = true;
			}
		}

		return foundTreasure;
	}

	private synchronized void updatePositions() {
		String message = "U";
		for (PlayerThread p : players) {
			message += p.getXpos() + "," + p.getYpos() + "," + p.getDirection() + "," + p.getPoint() + "#";
		}
		notifyAllPlayers(message.substring(0, message.length() - 1));
	}

	public synchronized void notifyAllPlayers(String message) {
		try {
			for (PlayerThread p : players) {
				p.sendMessage(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String[][] genRandMaze(int size) {
		floor = 0;
		String[][] maze = new String[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int randNum = (int) (Math.random() * 200);
				if (i != 0 && i != (size - 1) && j != 0 && j != (size - 1)) {
					if (randNum <= 40) {
						maze[i][j] = "w";
					} else {
						maze[i][j] = " ";
						floor++;
					}
				}
			}
			maze[0][i] = "w";
			maze[size - 1][i] = "w";
			maze[i][0] = "w";
			maze[i][size - 1] = "w";
		}
		return maze;
	}

	private void checkClosed(String[][] maze) {
		visitedCell = 0;
		visited = new boolean[size][size];
		boolean foundEmpty = false;
		int firstX = 1;
		int firstY = 1;
		while (!foundEmpty && firstX < size - 1) {
			while (!foundEmpty && firstY < size - 1) {
				if (maze[firstX][firstY].equals(" ")) {
					foundEmpty = true;
				} else {
					firstY++;
				}
			}
			if (!foundEmpty) {
				firstX++;
			}
		}
		// System.out.println("Start at (" + firstX + ", " + firstY + ")");
		checkClosed(maze, firstX, firstY);
		// System.out.println(closed);
	}

	private void checkClosed(String[][] maze, int x, int y) {
		if (!maze[x][y].equals("w")) {
			// System.out.println("Found wall at: " + x + ", " + y);
			// System.out.println(visited[x][y]);
			visited[x][y] = true;
			visitedCell++;
			// System.out.println("Checking (" + x + ", " + y + ")");

			// Check up
			if (x > 1 && !visited[x - 1][y]) {
				// System.out.println("UP");
				checkClosed(maze, x - 1, y);
			}

			// Check left
			if (y > 1 && !visited[x][y - 1]) {
				// System.out.println("LEFT");
				checkClosed(maze, x, y - 1);
			}

			// Check down
			if (x < size - 2 && !visited[x + 1][y]) {
				// System.out.println("DOWN");
				checkClosed(maze, x + 1, y);
			}

			// Check right
			if (y < size - 2 && !visited[x][y + 1]) {
				// System.out.println("RIGHT");
				checkClosed(maze, x, y + 1);
			}
		}
	}
}
