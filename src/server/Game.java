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
        // TODO hardcoded version, skal ændres til en der finder en ledig plads
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
     * @throws Exception
     */
    public synchronized void receiveMessage(String message, PlayerThread player) throws Exception {
        int y = player.getYpos();
        int x = player.getXpos();
        switch (message.charAt(0)) {
        case 'U': {// Up
            //check om der er en væg (og senere hero) og at man ikke går udenfor banen
            
            if (!boardArray[y - 1][x].equals("w")) {
                player.setYpos(y - 1);
                player.setDirection("U");
                player.increasePoints(1);
            }
            else {
                player.reducePoints(1);
            }
            break;
        }
        case 'D': {// Down
            if (!boardArray[y + 1][x].equals("w")) {
                player.setYpos(y + 1);
                player.setDirection("D");
                player.increasePoints(1);
            }
            else {
                player.reducePoints(1);
            }
            break;
        }
        case 'R': {// Right
            
            if (!boardArray[y][x + 1].equals("w")) {
                player.setXpos(x + 1);
                player.setDirection("R");
                player.increasePoints(1);
            }
            else {
                player.reducePoints(1);
            }
            break;
        }
        case 'L': {// Left
            if (!boardArray[y][x - 1].equals("w")) {
                player.setXpos(x - 1);
                player.setDirection("L");
                player.increasePoints(1);
            }
            else {
                player.reducePoints(1);
            }
            break;
        }
        case 'N': {
            player.setPlayerName(message.substring(1));
            break;
        }
        case 'X': {
            System.out.println("exit");//TODO exit
            break;
        }
        default: {
            throw new Exception("default i receiveMessage");
        }
        }
    }
    
    public void notifyPlayers() throws IOException {
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
