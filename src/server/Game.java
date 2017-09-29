package server;

import java.io.IOException;
import java.util.ArrayList;

public class Game {
    private String board;
    private ArrayList<PlayerThread> players;
    
    public Game(String boardString) {
        this.players = new ArrayList<>();
        this.board = boardString;
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
        switch (message.charAt(0)) {
        case 'U': {// Up
            //check om der er en væg (og senere hero) og at man ikke går udenfor banen
            int y = player.getYpos();
            if (board.charAt(y - 20) != 'w') {
                player.setYpos(y - 1);
            }
            else {
                player.reducePoints(1);
            }
            break;
        }
        case 'D': {// Down
            int y = player.getYpos();
            if (board.charAt(y + 20) != 'w') {
                player.setYpos(y + 1);
            }
            else {
                player.reducePoints(1);
            }
            break;
        }
        case 'R': {// Right
            int x = player.getXpos();
            if (board.charAt(x + 1) != 'w') {
                player.setXpos(x + 1);
            }
            else {
                player.reducePoints(1);
            }
            break;
        }
        case 'L': {// Left
            int x = player.getXpos();
            if (board.charAt(x - 1) != 'w') {
                player.setXpos(x - 1);
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
