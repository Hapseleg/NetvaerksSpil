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
    private ArrayList<Treasure> treasures;
    
    public Game(String boardString) {
        this.players = new ArrayList<>();
        this.treasures = new ArrayList<>();
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
            spawnTreasure();//TODO bare for at teste spawn treasure
            addPlayerToBoard(player);
            this.players.add(player);
            player.sendMessage(board.toString());

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

    private synchronized void spawnTreasure() {
        boolean validPos = false;
        int x = 0, y = 0, points = rand.nextInt(500) + 100;//TODO points for treasure

        while (!validPos) {
            x = rand.nextInt(xSize - 1) + 1;
            y = rand.nextInt(xSize - 1) + 1;

            if (!boardArray[y][x].equals("w")) {
                boolean objectFoundAtPos = false;
                int i = 0;
                while (!objectFoundAtPos && i < players.size()) {
                    if (players.get(i).getXpos() == x && players.get(i).getYpos() == y) {
                        objectFoundAtPos = true;
                    }
                    i++;
                }
                i = 0;
                while (!objectFoundAtPos && i < treasures.size()) {
                    if (treasures.get(i).getX() == x && treasures.get(i).getY() == y) {
                        objectFoundAtPos = true;
                    }
                    i++;
                }
                validPos = !objectFoundAtPos;
            }
        }
        treasures.add(new Treasure(x, y, points));
    }

//    private synchronized void shotFired(PlayerThread player){
//
//    }
//
//    private synchronized int[] calculateShotFired(int x, int y, String Direction){
//        int[] a = new int[2];
//
//        if(x-1 > 0 &&)
//
//
//        return a;
//    }
    
    /**
     *
     * @param message
     * @param player
     * @throws IOException
     * @throws Exception
     */
    public synchronized void receiveMessage(String message, PlayerThread player)
        throws IOException {
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
            boolean nameTaken = false;
            for (PlayerThread p : players) {
                if (p.getName() != null && p.getName().equals(message.substring(1))) {
                    nameTaken = true;
                    break;
                }
            }
            if (nameTaken) {
                player.sendMessage("0");
            }
            else {
                player.sendMessage("1");
                player.setPlayerName(message.substring(1));
                
                String c = "C";
                for (PlayerThread p : players) {
                    if (!p.equals(player)) {
                        c += p.getPlayerName() + ",";
                    }
                }
                player.sendMessage(c + player.getPlayerName());

                //send navnene på de spillere der allerede er i spillet
                for (PlayerThread p : players) {
                    if (!p.equals(player)) {
                        p.sendMessage("J" + player.getPlayerName());
                    }
                }
                updatePositions();
            }
            break;
        }
        case 'X': {
            players.remove(player);
            player.setKeepRunning(false);
            break;
        }
        case 'S': {
//            shotFired(player);
        }
        default: {
            System.out.println("Default i game.receiveMessage");
        }
        }
    }
    
    private void movePlayer(int x, int y, String direction, PlayerThread player)
        throws IOException {
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

        //U17,6,U,4#13,8,D,0
        updatePositions();
    }
    
    public void removePlayer(PlayerThread player) {
        players.remove(player);
        notifyAllPlayers("L" + player.getName());
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

    private synchronized void updatePositions() {
        String message = "U";
        for (PlayerThread p : players) {
            message +=
                p.getXpos() + "," + p.getYpos() + "," + p.getDirection() + "," + p.getPoint() + "#";
        }
        notifyAllPlayers(message.substring(0, message.length() - 1));
    }
    
    public synchronized void notifyAllPlayers(String message) {
//        String s = "";
//        for (PlayerThread p : players) {
//            s += p.getPlayerName() + "," + p.getXpos() + "," + p.getYpos() + "," + p.getDirection()
//                + "," + p.getPoint() + "#";
//        }

        try {
            for (PlayerThread p : players) {
                p.sendMessage(message);
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //TODO vi har mulighed for at gøre den sendte besked mindre,
    /*
     *Hvis vi kun sender navnet første gang der er en ny spiller, det kan gøres ved at have et array
     *på hver client der holder på navnene og så for at kende forskel på om det er en normal besked
     *eller en besked med opdatering i spillere (sker både ved remove og add player)
     *kan vores beskeder fx være:
     *Derved slipper vi for en hel del data da den ikke skal sende navnene med hele tiden
     */

    /*
     *Protokoller:
     *  LCasper   (sendes når en leaver)
     *  JCasper,xx,yy,d,p     (sendes når en joiner til dem der allerede er i spillet)
     *  CCasper,Bob,Mike        (Sendes til en der lige er joinet)
     *  U17,6,U,4#13,8,D,0    (sendes når nogen går eller point skifter)
     *  Txx,yy    (treasure spawner)
     *  Sxx,yy,xx,yy  (laser skud, xy start og xy slut)
     */
    
}
