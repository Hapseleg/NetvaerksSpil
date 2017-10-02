package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ReadThread extends Thread {

	private Socket socket;
	private BufferedReader inFromServer;
	private Label[][] fields;
	private TextArea scoreboard;
	private ArrayList<Label> usedLabels;
	private ArrayList<String> players, tempNames;

	private Image hero_right, hero_left, hero_up, hero_down, floor, treasure;

	private boolean running;

	public ReadThread(Socket socket, BufferedReader inFromServer, Label[][] fields, TextArea scoreboard) {
		this.socket = socket;
		this.inFromServer = inFromServer;
		this.fields = fields;
		this.scoreboard = scoreboard;
		hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), 20, 20, false, false);
		hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), 20, 20, false, false);
		hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), 20, 20, false, false);
		hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), 20, 20, false, false);
		floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), 20, 20, false, false);
		usedLabels = new ArrayList<>();
		running = true;
		this.players = new ArrayList<String>();
		this.tempNames = new ArrayList<String>();
	}

	@Override
	public void run() {
		while (running) {
			try {
				String input = inFromServer.readLine();
				System.out.println(input);
				String protocol = input.substring(0, 1);
				input = input.substring(1);
				switch (protocol) {
				case "L":
					players.remove(input);
					break;
				case "J":
					players.add(input);
					break;
				case "C":
					String[] playerArray = input.split(",");
					for (String s : playerArray) {
						players.add(s);
					}
					break;
				case "U":
					String[] updateArray = input.split("#");
					Platform.runLater(() -> {
						scoreboard.setText("");
						for (Label l : usedLabels) {
							l.setGraphic(new ImageView(floor));
						}
						usedLabels = new ArrayList<>();
					});
					for (int i = 0; i < updateArray.length; i++) {
						String[] eachPlayer = updateArray[i].split(",");
						ImageView dir;
						tempNames.add(players.get(i));
						switch (eachPlayer[2]) {
						case "U":
							dir = new ImageView(hero_up);
							break;
						case "D":
							dir = new ImageView(hero_down);
							break;
						case "L":
							dir = new ImageView(hero_left);
							break;
						case "R":
							dir = new ImageView(hero_right);
							break;
						default:
							dir = new ImageView(hero_up);
							break;
						}
						Platform.runLater(() -> {
							scoreboard.setText(scoreboard.getText() + tempNames.get(0) + ": " + eachPlayer[3] + "\n");
							usedLabels.add(fields[Integer.parseInt(eachPlayer[0])][Integer.parseInt(eachPlayer[1])]);
							fields[Integer.parseInt(eachPlayer[0])][Integer.parseInt(eachPlayer[1])].setGraphic(dir);
							tempNames.remove(0);
						});
					}
					break;
				case "T":
					System.out.println("My Precious!");
					break;
				case "S":
					System.out.println("Pew Pew");
					break;
				default:
					System.out.println("Didn't recognize message");
					break;
				}
				// String[] firstSplit = input.split("#");
				// Platform.runLater(() -> {
				// scoreboard.setText("");
				// for (Label l : usedLabels) {
				// l.setGraphic(new ImageView(floor));
				// }
				// usedLabels = new ArrayList<>();
				// });
				// for (int i = 0; i < firstSplit.length; i++) {
				// String[] eachPlayer = firstSplit[i].split(",");
				// ImageView dir;
				// switch (eachPlayer[2]) {
				// case "U":
				// dir = new ImageView(hero_up);
				// break;
				// case "D":
				// dir = new ImageView(hero_down);
				// break;
				// case "L":
				// dir = new ImageView(hero_left);
				// break;
				// case "R":
				// dir = new ImageView(hero_right);
				// break;
				// default:
				// dir = new ImageView(hero_up);
				// }
				// Platform.runLater(() -> {
				//
				// scoreboard.setText(scoreboard.getText() + eachPlayer[0] + ":
				// " + eachPlayer[4] + "\n");
				// usedLabels.add(fields[Integer.parseInt(eachPlayer[1])][Integer.parseInt(eachPlayer[2])]);
				// fields[Integer.parseInt(eachPlayer[1])][Integer.parseInt(eachPlayer[2])].setGraphic(dir);
				// });
				// }
			} catch (IOException e) {
				running = false;
			}
		}
	}
}
