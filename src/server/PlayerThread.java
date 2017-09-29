package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PlayerThread extends Thread {
    private Socket connectionSocket;
    private Game game;
    private int xpos, ypos, point;
    private String direction, playerName;
    private DataOutputStream outToClient;

    public PlayerThread(Socket connectionSocket, Game game, int xpos, int ypos, String direction) {
        this.connectionSocket = connectionSocket;
        this.game = game;
        this.direction = direction;
        
        try {
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        game.addPlayer(this);
    }
    
    @Override
    public void run() {
        try {
            BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            while (true) {
                String s = inFromClient.readLine();
                System.out.println(s);
                try {
                    game.receiveMessage(s, this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMessage(String message) throws IOException {
        outToClient.writeBytes(message + '\n');
    }

    public int getXpos() {
        return xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public int getPoint() {
        return point;
    }

    public String getDirection() {
        return direction;
    }
    
    public void setXpos(int xpos) {
        this.xpos = xpos;
    }
    
    public void setYpos(int ypos) {
        this.ypos = ypos;
    }
    
    public void setPoint(int point) {
        this.point = point;
    }

    public void reducePoints(int reduction) {
        this.point -= reduction;
    }
    
    public void increasePoints(int addition) {
        this.point += addition;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
}
