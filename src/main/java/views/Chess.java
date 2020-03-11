package views;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import chess.Board;
import com.fasterxml.jackson.databind.ObjectMapper;

import chess.CPU;
import chess.Cell;
import chess.Move;
import game.ICPU;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import player.Player;
import player.PlayerType;

interface CellClickedHandler {
    void handleCellClicked(Cell cell);
}

public class Chess {
    public final static String PLAYER1_CHAR = "w";
    public final static String PLAYER2_CHAR = "b";

    // Game logic
    private Player player1;
    private Player player2;

    private Player currentPlayer;

    private Cell[][] board = new Cell[8][8];
    private List<Move> activeMoves = new ArrayList<>();
    private Cell movingCell = null;

    private ICPU cpu1;
    private ICPU cpu2;

    private long lastUpdate = 0;

    boolean checkMate = false;

    // UI
    private CellPane[][] boardDisplay = new CellPane[8][8];
    private Label status = new Label();
    private Label checkText = new Label();

    private CellClickedHandler cellClickedHandler;

    // Needed for network games
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private ObjectMapper objectMapper;

    private boolean gameReady = true;
    private boolean sendNetworkData = true;

    // Constructors
    public Chess(PlayerType opponentType) {
        this(opponentType, null);
    }

    public Chess(PlayerType opponentType, Socket socket) {
        player1 = new Player(PlayerType.HUMAN, PLAYER1_CHAR);
        player2 = new Player(opponentType, PLAYER2_CHAR);
        currentPlayer = player2;

        cpu1 = new CPU(board, player1, player2);
        cpu2 = new CPU(board, player2, player1);

        // Set up the network if needed
        if (opponentType == PlayerType.NETWORK) {
            gameReady = false;

            try {
                if (socket != null) {
                    this.socket = socket;
                } else {
                    this.socket = new Socket("localhost", 8000);
                }

                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
                objectMapper = new ObjectMapper();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cellClickedHandler = cell -> handleCellClick(cell);
    }

    public Scene getScene(EventHandler<ActionEvent> exitHandler) {
        GridPane pane = new GridPane();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Cell(i, j);
                pane.add(boardDisplay[i][j] = new CellPane(board[i][j], cellClickedHandler), j, i);
            }
        }

        setUpBoard();

        status.setFont(Font.font("Times New Roman", 24));
        checkText.setFont(Font.font("Times New Roman", 24));

        Button resetButton = new Button("Play Again");
        resetButton.setOnMouseClicked(e -> resetGame());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(exitHandler);

        StackPane bottomRow = new StackPane();

        bottomRow.getChildren().add(exitButton);
        bottomRow.getChildren().add(status);
        bottomRow.getChildren().add(checkText);
        StackPane.setAlignment(exitButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(status, Pos.CENTER);
        StackPane.setAlignment(checkText, Pos.CENTER_RIGHT);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);
        borderPane.setBottom(bottomRow);

        return new Scene(borderPane, 600, 600);
    }

    public void start() {
        if (!gameReady) {
            status.setText("Waiting on player...");
        }

        if (player2.getType() == PlayerType.NETWORK) {
            // Wait for the player in a new thread
            new Thread(() -> {
                try {
                    // Wait for the game to start
                    String str;
                    while (!(str = inputStream.readUTF()).startsWith("start"))
                        ;

                    // Get the player id
                    int player = Integer.valueOf(str.substring(str.length() - 1));

                    System.out.println("Player id: " + player);

                    // If we are player 2, swap the players
                    if (player == 2) {
                        player1 = new Player(PlayerType.NETWORK, PLAYER1_CHAR);
                        player2 = new Player(PlayerType.HUMAN, PLAYER2_CHAR);

                        currentPlayer = player2;

                        // Don't send data at the beginning
                        sendNetworkData = false;
                    }

                    Platform.runLater(this::setUpBoard);
                    Platform.runLater(this::switchPlayerTurn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            switchPlayerTurn();
        }
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

        drawBoardPattern();
        refreshBoard();
    }

    public void refreshBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Swap the row and col if it should be
                int row = player1.getType() == PlayerType.NETWORK ? 7 - i : i;
                int col = player1.getType() == PlayerType.NETWORK ? 7 - j : j;

                // Check if this is an active move
                boolean moving = movingCell != null
                        && activeMoves.stream().anyMatch(move -> move.toCell == board[row][col]);

                boardDisplay[i][j].refreshCell(board[row][col], moving);
            }
        }
    }

    public void clearMoves() {
        activeMoves.clear();
        drawBoardPattern();
    }

    private void drawBoardPattern() {
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
        new Thread(() -> {
            ICPU cpu = getCurrentCPU();

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

            Platform.runLater(this::refreshBoard);
            lastUpdate = System.currentTimeMillis();

            Platform.runLater(this::switchPlayerTurn);
        }).start();
    }

    private void networkTurn() {
        try {
            // Send the board if needed
            if (sendNetworkData) {
                outputStream.writeUTF(objectMapper.writeValueAsString(board));
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Wait on the other player in a new thread
        new Thread(() -> {
            try {
                // Wait for the new board
                String str;
                while ((str = inputStream.readUTF()).equals(""))
                    ;

                board = objectMapper.readValue(str, Cell[][].class);

                // Always send data from now on
                sendNetworkData = true;

                Platform.runLater(this::refreshBoard);
                Platform.runLater(this::switchPlayerTurn);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void switchPlayerTurn() {
        // Check for win or check
        if (Board.isChecked(board, currentPlayer, getCurrentPlayerOpponent())) {
            checkText.setText("Check");
        } else {
            checkText.setText("");
        }

        if (Board.checkWin(board, currentPlayer)) {
            status.setText("Game over - " + (currentPlayer.getCharacter().equals("b") ? "Black" : "White") + " wins");
            return;
        }

        currentPlayer = currentPlayer == player1 ? player2 : player1;

        // Check for checkmate
        List<Move> moves = Board.getMoves(board, currentPlayer, getCurrentPlayerOpponent(), true);
        if (moves.size() == 0) {
            checkText.setText("Checkmate");
            checkMate = true;
        }

        if (currentPlayer.getType() == PlayerType.CPU) {
            status.setText("CPU is thinking...");
            cpuTurn();
        } else if (currentPlayer.getType() == PlayerType.NETWORK) {
            status.setText("Waiting on player...");
            networkTurn();
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
        }
    }

    public void handleCellClick(Cell cell) {
        if (getCurrentPlayer() != null && getCurrentPlayer().getType() == PlayerType.HUMAN) {
            String playerChar = getCurrentPlayer().getCharacter();

            if (movingCell != null && activeMoves.stream().anyMatch(move -> move.toCell == cell)) {
                Move move = activeMoves.stream().filter(m -> m.toCell == cell).findFirst().get();

                move.makeMove();
                movingCell = null;
                clearMoves();
                refreshBoard();
                lastUpdate = System.currentTimeMillis();

                // Switch turn
                switchPlayerTurn();
            } else if (cell.getToken().contains(playerChar)) {
                clearMoves();
                movingCell = cell;
                activeMoves.addAll(cell.findMoves(board, getCurrentPlayer(), getCurrentPlayerOpponent(), !checkMate));
                refreshBoard();
            } else {
                clearMoves();
                movingCell = null;
            }
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getCurrentPlayerOpponent() {
        return currentPlayer == player1 ? player2 : player1;
    }

    public ICPU getCurrentCPU() {
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
        ICPU cpu = chess.getCurrentCPU();

        System.out.println("Your best move is: " + cpu.getBestMove());
    }
}
