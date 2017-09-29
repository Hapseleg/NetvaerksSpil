package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Game {
    private String[][] board;
    private ArrayList<PlayerThread> players;

    public Game(String boardString) {
        this.players = new ArrayList<>();
        createBoard(boardString);
    }
    
    private void createBoard(String boardString) {
        board = new String[20][20];
        for (int j = 0; j < 20; j++) {
            String ar = boardString.substring(j * 20, (j + 1) * 20);
            for (int i = 0; i < 20; i++) {
                board[j][i] = ar.charAt(i) + "";
            }
            System.out.println(Arrays.toString(board[j]));
        }
    }

    public ArrayList<PlayerThread> getPlayers() {
        return players;
    }

    public void addPlayer(PlayerThread player) {
        this.players.add(player);
        try {
            addPlayerToBoard(player);
            player.sendMessage(board.toString());
            notifyPlayers();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPlayerToBoard(PlayerThread player) {
        // TODO hardcoded version, skal ændres til en der finder en ledig plads
        switch (players.size()) {
        case 1:
            player.setXpos(1);
            player.setYpos(1);
            break;

        case 2:
            player.setXpos(19);
            player.setYpos(19);
            break;

        case 3:
            // player.setXpos(1);
            // player.setYpos(1);
            break;

        case 4:

            break;

        default:
            break;
        }
        System.out.println("player x pos : " + player.getXpos());
    }

    /**
     *
     * @param message
     */
    public synchronized void receiveMessage(String message, PlayerThread player) {
        switch (message.charAt(0)) {
        case 'U': {// Up
            
            break;
        }
        case 'D': {// Down

            break;
        }
        case 'R': {// Right

            break;
        }
        case 'L': {// Left

            break;
        }
        case 'N':
            player.setPlayerName(message.substring(1));
            break;
        default: {

        }
        }
    }
    
    private synchronized boolean validateNewPos(int direction, int xPos, int yPos) {
        boolean validPos = false;
//        if()

        return validPos;
    }

    private void notifyPlayers() throws IOException {
        String s = "";
        for (PlayerThread p : players) {
            s += p.getPlayerName() + "," + p.getXpos() + "," + p.getYpos() + "," + p.getDirection()
                + "," + p.getPoint() + "#";
        }
        for (PlayerThread p : players) {
            p.sendMessage(s.substring(0, s.length() - 1));
        }
    }

}
