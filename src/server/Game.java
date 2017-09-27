package server;

import java.io.IOException;
import java.util.ArrayList;

public class Game {
    private String board;
    private ArrayList<PlayerThread> players;

    public Game(String board) {
        this.players = new ArrayList<>();
        this.board = board;
    }
    
    public ArrayList<PlayerThread> getPlayers() {
        return players;
    }
    
    public void addPlayer(PlayerThread player) {
        this.players.add(player);
        try {
            addPlayerToBoard(player);
            player.sendMessage(board);
            notifyPlayers();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void addPlayerToBoard(PlayerThread player) {
        //TODO hardcoded version, skal ændres til en der finder en ledig plads
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
//            player.setXpos(1);
//            player.setYpos(1);
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
    public void receiveMessage(String message, PlayerThread player) {
        switch (message.charAt(0)) {
        case 'U': {//Up

            break;
        }
        case 'D': {//Down

            break;
        }
        case 'R': {//Right

            break;
        }
        case 'L': {//Left

            break;
        }
        case 'N':
            player.setName(message.substring(1));
            break;
        default: {

        }
        }
    }

    private void notifyPlayers() throws IOException {
        String s = "";
        for (PlayerThread p : players) {
            s += p.getName() + "," + p.getXpos() + "," + p.getYpos() + "," + p.getPoint() + "#";
        }
        for (PlayerThread p : players) {
            p.sendMessage(s.substring(0, s.length() - 1));
        }
    }
    
}
