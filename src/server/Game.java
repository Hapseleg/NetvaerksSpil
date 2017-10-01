package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game {
    private String board;
    private String[][] boardArray;
    private ArrayList<PlayerThread> players;
    private Random rand;
    private int xSize, ySize;

    public Game(String boardString) {
        this.players = new ArrayList<>();
        this.board = boardString;
        rand = new Random();
        this.xSize = 20;
        this.ySize = 20;
        createBoardArray(xSize, ySize);
    }

    private void createBoardArray(int x, int y) {
        boardArray = new String[x][y];
        
        for (int i = 0; i < boardArray.length; i++) {
            boardArray[i] = board.substring(i * x, (i + 1) * y).split("(?!^)");
            System.out.println(Arrays.toString(boardArray[i]));
        }
    }
    
//    public ArrayList<PlayerThread> getPlayers() {
//        return players;
//    }

    public void addPlayer(PlayerThread player) {
        try {
            addPlayerToBoard(player);
            this.players.add(player);
            player.sendMessage(board.toString());
//            notifyPlayers();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void addPlayerToBoard(PlayerThread player) {
        boolean validPos = false;
        int x = 0, y = 0;
        
        while (!validPos) {
            x = rand.nextInt(xSize - 1) + 1;
            y = rand.nextInt(xSize - 1) + 1;
            
            if (!boardArray[y][x].equals("w")) {
                boolean playerFoundAtPos = false;
                int i = 0;
                while (!playerFoundAtPos && i < players.size()) {
                    if (players.get(i).getXpos() == x && players.get(i).getYpos() == y) {
                        playerFoundAtPos = true;
                    }
                    i++;
                }
                validPos = !playerFoundAtPos;
            }
        }
        player.setXpos(x);
        player.setYpos(y);
    }

    /**
     *
     * @param message
     * @param player
     * @throws Exception
     */
    public synchronized void receiveMessage(String message, PlayerThread player) {
        int x = player.getXpos();
        int y = player.getYpos();
        switch (message.charAt(0)) {
        case 'U': {// Up
            movePlayer(x, y - 1, "U", player);
            break;
        }
        case 'D': {// Down
            movePlayer(x, y + 1, "D", player);
            break;
        }
        case 'R': {// Right
            movePlayer(x + 1, y, "R", player);
            break;
        }
        case 'L': {// Left
            movePlayer(x - 1, y, "L", player);
            break;
        }
        case 'N': {
            player.setPlayerName(message.substring(1));
            break;
        }
        case 'X': {
            players.remove(player);
            player.setKeepRunning(false);
            break;
        }
        default: {
            System.out.println("Default i game.receiveMessage");
        }
        }
    }

    private void movePlayer(int x, int y, String direction, PlayerThread player) {
        if (boardArray[y][x].equals("w")) {
            player.reducePoints(1);
        }
        else if (checkForPlayer(x, y, player)) {
            player.increasePoints(50);
        }
        else {
            player.setYpos(y);
            player.setXpos(x);
            player.increasePoints(1);
        }
        player.setDirection(direction);
    }

    public void removePlayer(PlayerThread player) {
        players.remove(player);
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
        //TODO vi har mulighed for at gøre den sendte besked mindre,
        /*
         *Hvis vi kun sender navnet første gang der er en ny spiller, det kan gøres ved at have et array
         *på hver client der holder på navnene og så for at kende forskel på om det er en normal besked
         *eller en besked med opdatering i spillere (sker både ved remove og add player)
         *kan vores beskeder fx være:
         *XCasper,17,6,U,4#Casper,13,8,D,0 (ny/fjernet spiller)
         *Z17,6,U,4#13,8,D,0
         *Derved slipper vi for en hel del data da den ikke skal sende navnene med hele tiden
         */
    }

}
