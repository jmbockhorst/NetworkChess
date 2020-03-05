import com.fasterxml.jackson.databind.ObjectMapper;

import chess.CPU;
import chess.Cell;
import chess.Move;
import player.Player;
import player.PlayerType;
import views.Chess;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    public static void main(String[] args) {
        new GameServer();
    }

    ServerSocket serverSocket;
    Socket socket;

    public GameServer() {
        try {
            serverSocket = new ServerSocket(8000);

            while (true) {
                socket = serverSocket.accept();
                new Thread(new TwoPlayerConnectionHandler(socket, serverSocket.accept())).start();
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
            // Wait for the board from player 1
            String str;
            while ((str = inputStream1.readUTF()).equals(""))
                ;

            System.out.println("Received data from player 1");

            board = objectMapper.readValue(str, Cell[][].class);

            // Send the board to player 2
            outputStream2.writeUTF(objectMapper.writeValueAsString(board));
            outputStream2.flush();

            System.out.println("Sent data to player 2");

            // Wait for the board from player 2
            String str2;
            while ((str2 = inputStream2.readUTF()).equals(""))
                ;

            System.out.println("Received data from player 2");

            board = objectMapper.readValue(str2, Cell[][].class);

            // Send the board to player 1
            outputStream1.writeUTF(objectMapper.writeValueAsString(board));
            outputStream1.flush();

            System.out.println("Sent data to player 1");

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

class ConnectionHandler implements Runnable {
    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    ObjectMapper objectMapper;
    Cell[][] board;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;

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
        try {
            // Wait for the board
            String str;
            while ((str = inputStream.readUTF()).equals(""))
                ;

            board = objectMapper.readValue(str, Cell[][].class);

            // Run the CPU and send the new board
            runCPU();
            outputStream.writeUTF(objectMapper.writeValueAsString(board));
            outputStream.flush();

            run();
        } catch (IOException e) {
            e.printStackTrace();

            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void runCPU() {
        CPU cpu = new CPU(board, new Player(PlayerType.CPU, Chess.PLAYER2_CHAR),
                new Player(PlayerType.HUMAN, Chess.PLAYER1_CHAR));

        long startTime = System.currentTimeMillis();
        Move move = cpu.getBestMove();
        move.makeMove();

        // Always wait at least 1 second
        long calcTime = System.currentTimeMillis() - startTime;
        if (calcTime < 1000) {
            try {
                Thread.sleep(1000 - calcTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
