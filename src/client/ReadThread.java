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
	private int tX, tY, sI, sJ;

	private ArrayList<Integer> drawnShot;

	private Image hero_right, hero_left, hero_up, hero_down, floor, treasure, shotVert, shotHori;

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
		shotVert = new Image(getClass().getResourceAsStream("Image/fireVertical.png"), 20, 20, false, false);
		shotHori = new Image(getClass().getResourceAsStream("Image/fireHorizontal.png"), 20, 20, false, false);
		treasure = new Image(getClass().getResourceAsStream("Image/treasure.png"), 20, 20, false, false);
		usedLabels = new ArrayList<>();
		running = true;
		drawnShot = new ArrayList<Integer>();
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
					if (input.length() > 0) {
						System.out.println("My Precious!");
						String[] coords = input.split(",");
						tX = Integer.parseInt(coords[0]);
						tY = Integer.parseInt(coords[1]);
						Platform.runLater(() -> {
							fields[tX][tY].setGraphic(new ImageView(treasure));
						});
					} else {
						Platform.runLater(() -> {
							fields[tX][tY].setGraphic(new ImageView(floor));
						});
					}
					break;
				case "S":
					System.out.println("Pew Pew");
					String[] shot = input.split(",");
					// Få de 4 koordinator ud af array, så de lettere kan
					// bruges.
					int xStart = Integer.parseInt(shot[0]);
					int yStart = Integer.parseInt(shot[1]);
					int xEnd = Integer.parseInt(shot[2]);
					int yEnd = Integer.parseInt(shot[3]);
					String direction = shot[4];

					// Er det vertikal skud
					if (direction.equals("U") || direction.equals("D")) {

						// Ned
						if (yStart > yEnd) {
							sI = yEnd;
							sJ = yStart;

							// Op
						} else {
							sI = yStart;
							sJ = yEnd;
						}
						Platform.runLater(() -> {
							// Tegn laser
							while (sI <= sJ) {
								fields[xStart][sI].setGraphic(new ImageView(shotVert));

								// Tilføj koordinator så de kan fjernes igen
								drawnShot.add(xStart);
								drawnShot.add(sI);
								sI++;
							}
						});
						// Horisontal skud
					} else {

						// Venstre
						if (xStart > xEnd) {
							sI = xEnd;
							sJ = xStart;

							// Højre
						} else {
							sI = xStart;
							sJ = xEnd;
						}
						Platform.runLater(() -> {
							// Tegn laser
							while (sI <= sJ) {
								fields[sI][yStart].setGraphic(new ImageView(shotHori));

								// Tilføj koordinater så de kan fjernes igen
								drawnShot.add(sI);
								drawnShot.add(yStart);
								sI++;
							}
						});
					}
					// Vent 0.2 sekunder, og fjern skud fra bræt
					sleep(100);
					shotCancel();
					break;
				default:
					System.out.println("Didn't recognize message");
					break;
				}
			} catch (IOException e) {
				running = false;
			} catch (InterruptedException e) {
				System.out.println("Couldn't sleep");
			}
		}
	}

	// Hjælpe metode til at fjerne skud fra bræt
	private void shotCancel() {
		Platform.runLater(() -> {
			for (int i = 0; i < drawnShot.size(); i = i + 2) {
				fields[drawnShot.get(i)][drawnShot.get(i + 1)].setGraphic(new ImageView(floor));
			}
			drawnShot = new ArrayList<Integer>();
		});
	}
}
