package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Game {
    private String board;
    private String[][] boardArray;
    private ArrayList<PlayerThread> players;

    public Game(String boardString) {
        this.players = new ArrayList<>();
        this.board = boardString;
        createBoardArray(20, 20);
    }

    private void createBoardArray(int x, int y) {
        boardArray = new String[x][y];
        
        for (int i = 0; i < boardArray.length; i++) {
            boardArray[i] = board.substring(i * x, (i + 1) * y).split("(?!^)");
            System.out.println(Arrays.toString(boardArray[i]));
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
        // TODO hardcoded version, skal ændres til en der finder en random ledig plads
        switch (players.size()) {
        case 1:
            player.setXpos(1);
            player.setYpos(1);
            break;

        case 2:
            player.setXpos(18);
            player.setYpos(18);
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
     * @param player
     * @throws Exception
     */
    public synchronized void receiveMessage(String message, PlayerThread player) throws Exception {
        int x = player.getXpos();
        int y = player.getYpos();
        switch (message.charAt(0)) {
        case 'U': {// Up
            if (boardArray[y - 1][x].equals("w")) {
                player.reducePoints(1);
            }
            else if (checkForPlayer(x, y - 1, player)) {
                player.increasePoints(50);
            }
            else {
                player.setYpos(y - 1);
                player.increasePoints(1);
            }
            player.setDirection("U");
            break;
        }
        case 'D': {// Down
            if (boardArray[y + 1][x].equals("w")) {
                player.reducePoints(1);
            }
            else if (checkForPlayer(x, y + 1, player)) {
                player.increasePoints(50);
            }
            else {
                player.setYpos(y + 1);
                player.increasePoints(1);
            }
            player.setDirection("D");
            break;
        }
        case 'R': {// Right

            if (boardArray[y][x + 1].equals("w")) {
                player.reducePoints(1);
            }
            else if (checkForPlayer(x + 1, y, player)) {
                player.increasePoints(50);
            }
            else {
                player.setXpos(x + 1);
                player.increasePoints(1);
            }
            player.setDirection("R");
            break;
        }
        case 'L': {// Left
            if (boardArray[y][x - 1].equals("w")) {
                player.reducePoints(1);
            }
            else if (checkForPlayer(x - 1, y, player)) {
                player.increasePoints(50);
            }
            else {
                player.setXpos(x - 1);
                player.increasePoints(1);
            }
            player.setDirection("L");
            break;
        }
        case 'N': {
            player.setPlayerName(message.substring(1));
            break;
        }
        case 'X': {
            players.remove(player);
            player.stop();//TODO stop while loop
            break;
        }
        default: {
            throw new Exception("default i receiveMessage");
        }
        }
    }

    private boolean checkForPlayer(int x, int y, PlayerThread currentPlayer) {
        boolean foundPlayer = false;

        for (PlayerThread p : players) {
            if (!p.equals(currentPlayer)) {
                if (p.getXpos() == x && p.getYpos() == y) {
                    foundPlayer = true;
                    p.reducePoints(50);
                    break;
                }
            }
        }
        return foundPlayer;
    }

    public synchronized void notifyPlayers() throws IOException {
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
