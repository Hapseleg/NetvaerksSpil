package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainApp extends Application {

	public static final int size = 20;
	public static final int scene_height = size * 20 + 100;
	public static final int scene_width = size * 20 + 200;

	private Image image_floor;
	private Image image_wall;

	private String[] board;

	private Label[][] fields;
	private TextArea scoreList;
	private DataOutputStream outToServer;

	private ReadThread read;

	private Socket clientSocket;

	@Override
	public void start(Stage primaryStage) {
		try {
			IpNameWindow window = new IpNameWindow();
			window.showAndWait();

			clientSocket = new Socket(window.getIp(), 1337);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToServer.writeBytes("N" + window.getName() + "\n");
			String response = inFromServer.readLine();
			System.out.println(response);
			while (response.equals("0")) {
				window.nameTaken();
				window.showAndWait();
				outToServer.writeBytes("N" + window.getName() + "\n");
				response = inFromServer.readLine();
			}
			if (response.equals("0")) {
				stop();
			}
			board = updateBoard(inFromServer.readLine());
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();

			GridPane boardGrid = new GridPane();

			image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

			fields = new Label[20][20];
			for (int j = 0; j < 20; j++) {
				for (int i = 0; i < 20; i++) {
					switch (board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default:
						throw new Exception("Illegal field value: " + board[j].charAt(i));
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);
			read = new ReadThread(clientSocket, inFromServer, fields, scoreList);
			read.start();
			grid.add(mazeLabel, 0, 0);
			grid.add(scoreLabel, 1, 0);
			grid.add(boardGrid, 0, 1);
			grid.add(scoreList, 1, 1);

			Scene scene = new Scene(grid, scene_width, scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				try {
					switch (event.getCode()) {
					case UP:
						outToServer.writeBytes("U\n");
						break;
					case DOWN:
						outToServer.writeBytes("D\n");
						break;
					case LEFT:
						outToServer.writeBytes("L\n");
						break;
					case RIGHT:
						outToServer.writeBytes("R\n");
						break;
					case ESCAPE:
						stop();
						break;
					case SPACE:
						outToServer.writeBytes("S\n");
						break;
					case CONTROL:
						outToServer.writeBytes("S\n");
						break;
					default:
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String[] updateBoard(String s) {
		String[] board = new String[(int) Math.sqrt(s.length())];
		for (int i = 0; i < board.length; i++) {
			board[i] = s.substring(i * board.length, (i + 1) * board.length);
		}
		return board;
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() {
		try {
			outToServer.writeBytes("X\n");
			clientSocket.close();
		} catch (IOException e) {
			System.exit(1);
		}
	}
}
