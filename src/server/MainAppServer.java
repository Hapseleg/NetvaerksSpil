package server;

import java.net.ServerSocket;
import java.net.Socket;

public class MainAppServer {
    
    public static void main(String[] args) {
        String board =
            "wwwwwwwwwwwwwwwwwwwww        ww        ww w  w  www w  w  www w  w   ww w  w  www  w               ww w w w w w w  w  www w     www w  w  www w     w w w  w  www   w w  w  w  w   ww     w  w  w  w   ww ww ww        w  www  w w    w    w  www        ww w  w  www         w w  w  www        w     w  www  w              www  w www  w w  ww www w      ww w     www   w   ww  w      wwwwwwwwwwwwwwwwwwwww";

        Game game = new Game(board);
        
        try {
            ServerSocket welcomeSocket = new ServerSocket(1337);
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                PlayerThread player = new PlayerThread(connectionSocket, game, 0, 0, "D");
                
                player.start();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
