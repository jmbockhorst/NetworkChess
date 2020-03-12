package game.network;

import java.net.Socket;

public class NetworkGame {
    private int gameId;
    private String name;
    private Socket player1Socket;

    public NetworkGame(){

    }

    public NetworkGame(int gameId, String name, Socket player1Socket) {
        this.gameId = gameId;
        this.name = name;
        this.player1Socket = player1Socket;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getPlayer1Socket() {
        return player1Socket;
    }

    public void setPlayer1Socket(Socket player1Socket) {
        this.player1Socket = player1Socket;
    }
}
