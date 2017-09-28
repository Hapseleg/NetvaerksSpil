package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import javafx.scene.control.Label;

public class ReadThread extends Thread {

	private Socket socket;
	private BufferedReader inFromServer;
	private Label[][] fields;

	public ReadThread(Socket socket, BufferedReader inFromServer, Label[][] fields) {
		this.socket = socket;
		this.inFromServer = inFromServer;
		this.fields = fields;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String[] firstSplit = inFromServer.readLine().split("¤");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
