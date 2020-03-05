import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    public static void main(String[] args) throws IOException {
        new GameServer();
    }

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    ObjectMapper objectMapper;
    Cell[][] board;

    public GameServer(){
        try {
            serverSocket = new ServerSocket(8000);
            socket = serverSocket.accept();
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            objectMapper = new ObjectMapper();

            runGameLoop();

            socket.close();
            serverSocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void runGameLoop() throws Exception {
        // Wait for the board
        String str;
        while((str = inputStream.readUTF()).equals(""));

        System.out.println("Server received data");

        board = objectMapper.readValue(str, Cell[][].class);

        // Run the CPU and send the new board
        runCPU();
        outputStream.writeUTF(objectMapper.writeValueAsString(board));
        outputStream.flush();

        runGameLoop();
    }

    private void runCPU(){
        CPU cpu = new CPU(board, new Player(PlayerType.CPU, Chess.PLAYER2_CHAR), new Player(PlayerType.HUMAN, Chess.PLAYER1_CHAR));

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
