import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class Chess {
    final static String PLAYER1_CHAR = "w";
    final static String PLAYER2_CHAR = "b";

    private Player player1 = new Player(PlayerType.HUMAN, PLAYER1_CHAR);
    private Player player2;

    private Player currentPlayer;

    CellPane[][] boardDisplay = new CellPane[8][8];
    Cell[][] board = new Cell[8][8];
    List<Move> activeMoves = new ArrayList<>();

    Cell movingCell = null;

    boolean gameOver = false;
    Label status = new Label();

    CPU cpu1;
    CPU cpu2;

    long lastUpdate = 0;

    // Needed for network games
    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    ObjectMapper objectMapper;

    public Chess(PlayerType opponentType) {
        player2 = new Player(opponentType, PLAYER2_CHAR);
        currentPlayer = player2;

        // Set up the network if needed
        if (opponentType == PlayerType.NETWORK) {
            try {
                socket = new Socket("localhost", 8000);
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
                objectMapper = new ObjectMapper();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Scene getScene() {
        GridPane pane = new GridPane();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Cell(i, j);
                pane.add(boardDisplay[i][j] = new CellPane(this, board[i][j]), j, i);

                if (j % 2 == 0 ^ i % 2 == 0) {
                    boardDisplay[i][j].setStyle("-fx-background-color: #e29b3d");
                } else {
                    boardDisplay[i][j].setStyle("-fx-background-color: #f7ca8f");
                }
            }
        }

        setUpBoard();
        cpu1 = new CPU(board, player1, player2);
        cpu2 = new CPU(board, player2, player1);

        status.setFont(Font.font("Times New Roman", 24));

        Button resetButton = new Button("Play Again");
        resetButton.setOnMouseClicked(e -> resetGame());

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);
        borderPane.setBottom(status);
        BorderPane.setAlignment(status, Pos.CENTER);

        Scene scene = new Scene(borderPane, 600, 600);

        return scene;
    }

    public void start() {
        switchPlayerTurn();
    }

    public boolean checkWin(char token) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (token == 'b') {
                    if (board[i][j].getToken() == "wk") {
                        return false;
                    }
                } else if (token == 'w') {
                    if (board[i][j].getToken() == "bk") {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void resetGame() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j].setToken("");
            }
        }

        setUpBoard();
        currentPlayer = player1;
    }

    private void setUpBoard() {
        board[0][0].setToken("br");
        board[0][1].setToken("bn");
        board[0][2].setToken("bb");
        board[0][3].setToken("bq");
        board[0][4].setToken("bk");
        board[0][5].setToken("bb");
        board[0][6].setToken("bn");
        board[0][7].setToken("br");

        board[7][0].setToken("wr");
        board[7][1].setToken("wn");
        board[7][2].setToken("wb");
        board[7][3].setToken("wq");
        board[7][4].setToken("wk");
        board[7][5].setToken("wb");
        board[7][6].setToken("wn");
        board[7][7].setToken("wr");

        for (int i = 0; i < 8; i++) {
            board[1][i].setToken("bp");
            board[6][i].setToken("wp");
        }

        refreshBoard();
    }

    public void refreshBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardDisplay[i][j].refreshCell(board[i][j]);
            }
        }
    }

    public void clearMoves() {
        activeMoves.clear();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j % 2 == 0 ^ i % 2 == 0) {
                    boardDisplay[i][j].setStyle("-fx-background-color: #e29b3d");
                } else {
                    boardDisplay[i][j].setStyle("-fx-background-color: #f7ca8f");
                }
            }
        }
    }

    public void cpuTurn() {
        CPU cpu = getCurrentCPU();

        Move move = cpu.getBestMove();
        move.makeMove();

        // Always wait at least 1 second
        long calcTime = System.currentTimeMillis() - lastUpdate;
        if (calcTime < 1000) {
            try {
                Thread.sleep(1000 - calcTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        refreshBoard();
        lastUpdate = System.currentTimeMillis();

        if (move.toCell.getToken().endsWith("k")) {
            gameOver = true;
        }

        if (gameOver) {
            status.setText("GAME OVER! Black wins");
            currentPlayer = null;
        } else {
            Platform.runLater(() -> switchPlayerTurn());
        }
    }

    private void networkTurn() {
        try {
            // Send the board
            outputStream.writeUTF(objectMapper.writeValueAsString(board));
            outputStream.flush();

            System.out.println("Game sent data");

            // Wait for the new board
            String str = "";
            while ((str = inputStream.readUTF()).equals(""));

            board = objectMapper.readValue(str, Cell[][].class);
            refreshBoard();

            switchPlayerTurn();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void switchPlayerTurn() {
        currentPlayer = currentPlayer == player1 ? player2 : player1;

        if (currentPlayer.getType() == PlayerType.CPU) {
            status.setText("CPU is thinking...");
            Platform.runLater(() -> cpuTurn());
        } else if (currentPlayer.getType() == PlayerType.NETWORK) {
            status.setText("Waiting on player...");

            Platform.runLater(() -> networkTurn());
        } else {
            if (player1.getType() == PlayerType.HUMAN && player2.getType() == PlayerType.HUMAN) {
                if (currentPlayer == player1) {
                    status.setText("Player 1's turn");
                } else {
                    status.setText("Player 2's turn");
                }
            } else {
                status.setText("Your turn");
            }

            // new GetBestHumanMove(this).start();
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getCurrentPlayerOpponent() {
        return currentPlayer == player1 ? player2 : player1;
    }

    public CPU getCurrentCPU() {
        return currentPlayer == player1 ? cpu1 : cpu2;
    }
}

class GetBestHumanMove extends Thread {
    private Chess chess;

    public GetBestHumanMove(Chess chess) {
        this.chess = chess;
    }

    @Override
    public void run() {
        CPU cpu = chess.getCurrentCPU();

        System.out.println("Your best move is: " + cpu.getBestMove());
    }
}
