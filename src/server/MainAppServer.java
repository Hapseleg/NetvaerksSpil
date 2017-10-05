package server;

import java.net.ServerSocket;
import java.net.Socket;

public class MainAppServer {

	public static void main(String[] args) {
		Game game = new Game();

		try {
			ServerSocket welcomeSocket = new ServerSocket(1337);
			while (true) {
				Socket connectionSocket = welcomeSocket.accept();
				PlayerThread player = new PlayerThread(connectionSocket, game, 0, 0, "D");

				player.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
