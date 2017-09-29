package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

public class ReadThread extends Thread {

	private Socket socket;
	private BufferedReader inFromServer;
	private Label[][] fields;
	private TextArea scoreboard;

	public ReadThread(Socket socket, BufferedReader inFromServer, Label[][] fields, TextArea scoreboard) {
		this.socket = socket;
		this.inFromServer = inFromServer;
		this.fields = fields;
		this.scoreboard = scoreboard;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String input = inFromServer.readLine();
				String[] firstSplit = input.split("#");
				System.out.println(Arrays.toString(firstSplit));
				scoreboard.setText("");
				for (int i = 0; i < firstSplit.length; i++) {
					String[] eachPlayer = firstSplit[i].split(",");
					scoreboard.setText(scoreboard.getText() + eachPlayer[0] + ": " + eachPlayer[4] + "\n");
					fields[Integer.parseInt(eachPlayer[1])][Integer.parseInt(eachPlayer[2])]
							.setGraphic(new ImageView("hero_" + eachPlayer[3]));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
