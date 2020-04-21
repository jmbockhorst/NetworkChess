import com.fasterxml.jackson.databind.ObjectMapper;
import game.Cell;
import game.network.NetworkGame;
import game.network.NetworkGameClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameServer {
    public static void main(String[] args) {
        new GameServer();
    }

    ServerSocket serverSocket;
    List<NetworkGame> availableGames;

    public GameServer() {
        try {
            serverSocket = new ServerSocket(8000);
            availableGames = new ArrayList<>();

            while (true) {
                // Handle when a new connection is made
                new Thread(new NetworkGameHandler(serverSocket.accept(), availableGames)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();

            try {
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

class NetworkGameHandler implements Runnable {
    private final Socket socket;
    private final List<NetworkGame> availableGames;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private ObjectMapper objectMapper;
    private NetworkGame createdGame = null;

    public NetworkGameHandler(Socket socket, List<NetworkGame> availableGames) {
        this.socket = socket;
        this.availableGames = availableGames;

        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            objectMapper = new ObjectMapper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean waitingToStart = true;

        while (waitingToStart) {
            try {
                String str;
                while ((str = inputStream.readUTF()).equals("")) {
                    // Keep waiting until another player has joined
                    if (createdGame != null && availableGames.indexOf(createdGame) < 0) {
                        // Another player has joined
                        waitingToStart = false;
                    }
                }

                // Send the list of games
                if (str.equals("listGames")) {
                    System.out.println("Server - list games: " + availableGames.size());

                    // Create the list of games to send to the client
                    List<NetworkGameClient> clientGames = availableGames.stream()
                            .map(game -> new NetworkGameClient(game.getGameId(), game.getName()))
                            .collect(Collectors.toList());

                    outputStream.writeUTF(objectMapper.writeValueAsString(clientGames));
                    outputStream.flush();
                }

                // Process the join game request
                if (str.startsWith("join")) {
                    int gameId = Integer.valueOf(str.substring(str.length() - 1));
                    System.out.println("Server - join game: " + gameId);

                    // Start the game thread
                    NetworkGame gameToJoin = availableGames.stream().filter(game -> game.getGameId() == gameId)
                            .findFirst().get();
                    new Thread(new TwoPlayerConnectionHandler(gameToJoin.getPlayer1Socket(), socket)).start();

                    availableGames.removeIf(game -> game.getGameId() == gameId);

                    waitingToStart = false;
                }

                // Process the create game request
                if (str.startsWith("create")) {
                    System.out.println("Server - create game");
                    int nextGameId = availableGames.size() == 0 ? 0
                            : availableGames.stream().mapToInt(NetworkGame::getGameId).max().getAsInt() + 1;
                    String gameName = str.split("-")[1];

                    NetworkGame game = new NetworkGame(nextGameId, gameName, socket);
                    availableGames.add(game);
                    createdGame = game;
                }
            } catch (Exception e) {
                System.out.println("Client disconnected");
                if (createdGame != null) {
                    availableGames.removeIf(game -> game.getGameId() == createdGame.getGameId());
                }

                waitingToStart = false;

                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

class TwoPlayerConnectionHandler implements Runnable {
    Socket socket1;
    Socket socket2;
    DataInputStream inputStream1;
    DataOutputStream outputStream1;
    DataInputStream inputStream2;
    DataOutputStream outputStream2;
    ObjectMapper objectMapper;
    Cell[][] board;

    public TwoPlayerConnectionHandler(Socket socket1, Socket socket2) {
        this.socket1 = socket1;
        this.socket2 = socket2;

        System.out.println("Server connected");

        try {
            inputStream1 = new DataInputStream(socket1.getInputStream());
            outputStream1 = new DataOutputStream(socket1.getOutputStream());
            inputStream2 = new DataInputStream(socket2.getInputStream());
            outputStream2 = new DataOutputStream(socket2.getOutputStream());
            objectMapper = new ObjectMapper();

            outputStream1.writeUTF("start - player1");
            outputStream2.writeUTF("start - player2");

            outputStream1.flush();
            outputStream2.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Wait for the board from game.player 1
            String str;
            while ((str = inputStream1.readUTF()).equals(""))
                ;

            System.out.println("Received data from game.player 1");

            board = objectMapper.readValue(str, Cell[][].class);

            // Send the board to game.player 2
            outputStream2.writeUTF(objectMapper.writeValueAsString(board));
            outputStream2.flush();

            System.out.println("Sent data to game.player 2");

            // Wait for the board from game.player 2
            String str2;
            while ((str2 = inputStream2.readUTF()).equals(""))
                ;

            System.out.println("Received data from game.player 2");

            board = objectMapper.readValue(str2, Cell[][].class);

            // Send the board to game.player 1
            outputStream1.writeUTF(objectMapper.writeValueAsString(board));
            outputStream1.flush();

            System.out.println("Sent data to game.player 1");

            run();
        } catch (IOException e) {
            e.printStackTrace();

            try {
                socket1.close();
                socket2.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
