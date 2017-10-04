package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PlayerThread extends Thread {
	private Socket connectionSocket;
	private Game game;
	private int xpos, ypos, point;
	private String direction, playerName;
	private DataOutputStream outToClient;
	private boolean keepRunning;

	public PlayerThread(Socket connectionSocket, Game game, int xpos, int ypos, String direction) {
		keepRunning = true;
		this.connectionSocket = connectionSocket;
		this.game = game;
		this.direction = direction;

		try {
			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			while (keepRunning) {
				// TODO hvis man lukker client ned fra eclipse kommer der en
				// "java.net.SocketException: Connection reset"
				// if (connectionSocket.isClosed()) {
				// game.removePlayer(this);
				// break;
				// }
				System.out.println("Venter på besked");
				String messageFromClient = inFromClient.readLine();
				System.out.println("playerthread.run: " + messageFromClient);

				if (messageFromClient == null) {
					game.removePlayer(this);
					keepRunning = false;
					break;
				}

				game.receiveMessage(messageFromClient, this);
				// game.notifyPlayers();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) throws IOException {
		outToClient.writeBytes(message + '\n');
	}

	public int getXpos() {
		return xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public int getPoint() {
		return point;
	}

	public String getDirection() {
		return direction;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public void reducePoints(int reduction) {
		this.point -= reduction;
	}

	public void increasePoints(int addition) {
		this.point += addition;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setKeepRunning(boolean keepRunning) {
		this.keepRunning = keepRunning;
	}

}
