package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import game.ui.CellClickedHandler;
import game.ui.CellPane;
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
import javafx.stage.Stage;
import game.player.Player;
import game.player.PlayerType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * BoardGame
 */
public abstract class BoardGame {

    // Game logic
    protected Player player1;
    protected Player player2;

    protected Player currentPlayer;

    protected Cell[][] board;
    protected List<Move> activeMoves = new ArrayList<>();
    protected Cell movingCell = null;

    private CPU cpu1;
    private CPU cpu2;

    private long lastUpdate = 0;

    // UI
    protected CellPane[][] boardDisplay;
    private Label status = new Label();
    protected Label messageText = new Label();

    private CellClickedHandler cellClickedHandler;

    // Needed for network games
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private ObjectMapper objectMapper;

    private boolean gameReady = true;
    private boolean sendNetworkData = true;

    public BoardGame(int boardWidth, int boardHeight, String player1Char, String player2Char, PlayerType opponentType,
            Socket socket) {
        boardDisplay = new CellPane[boardHeight][boardWidth];
        board = new Cell[boardHeight][boardWidth];

        player1 = new Player(PlayerType.HUMAN, player1Char);
        player2 = new Player(opponentType, player2Char);
        currentPlayer = player2;

        cpu1 = new CPU(board, player1, player2, this::getPieceValue, this::getMoves);
        cpu2 = new CPU(board, player2, player1, this::getPieceValue, this::getMoves);

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

    public abstract void renderCell(CellPane cellPane, Cell cell);

    public abstract void setupBoardTokens();

    public abstract List<Move> getValidMoves(Cell cell, Player player, Player opponent, boolean noLosingMoves);

    public abstract boolean checkWin(Player player);

    public abstract int getPieceValue(String token, String opponentChar);

    public void handleTurnBegins() {
    }

    public boolean noLosingMoves() {
        return false;
    }

    public void handleMoveMade(Cell cell) {

    }

    public void render(Stage stage, EventHandler<ActionEvent> exitHandler) {
        GridPane pane = new GridPane();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = new Cell(i, j);
                pane.add(boardDisplay[i][j] = new CellPane(board[i][j], cellClickedHandler), j, i);
            }
        }

        setUpBoard();

        status.setFont(Font.font("Times New Roman", 24));
        messageText.setFont(Font.font("Times New Roman", 24));

        Button resetButton = new Button("Play Again");
        resetButton.setOnMouseClicked(e -> resetGame());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(exitHandler);

        StackPane bottomRow = new StackPane();

        bottomRow.getChildren().add(exitButton);
        bottomRow.getChildren().add(status);
        bottomRow.getChildren().add(messageText);
        StackPane.setAlignment(exitButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(status, Pos.CENTER);
        StackPane.setAlignment(messageText, Pos.CENTER_RIGHT);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);
        borderPane.setBottom(bottomRow);

        stage.setScene(new Scene(borderPane, 600, 600));
    }

    public void renderBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                // Swap the row and col if it should be
                int row = player1.getType() == PlayerType.NETWORK ? board.length - 1 - i : i;
                int col = player1.getType() == PlayerType.NETWORK ? board[0].length - 1 - j : j;

                // Check if this is an active move
                boolean moving = movingCell != null
                        && activeMoves.stream().anyMatch(move -> move.toCell == board[row][col]);

                renderCell(boardDisplay[i][j], board[row][col]);
                boardDisplay[i][j].refreshCell(board[row][col], moving);
            }
        }
    }

    private void setUpBoard() {
        setupBoardTokens();
        renderBoard();
    }

    public void resetGame() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j].setToken("");
            }
        }

        setUpBoard();
        currentPlayer = player1;
    }

    public void start() {
        if (!gameReady) {
            status.setText("Waiting on game.player...");
        }

        if (player2.getType() == PlayerType.NETWORK) {
            // Wait for the game.player in a new thread
            new Thread(() -> {
                try {
                    // Wait for the game to start
                    String str;
                    while (!(str = inputStream.readUTF()).startsWith("start"))
                        ;

                    // Get the game.player id
                    int player = Integer.valueOf(str.substring(str.length() - 1));

                    System.out.println("Player id: " + player);

                    // If we are game.player 2, swap the players
                    if (player == 2) {
                        player1 = new Player(PlayerType.NETWORK, player1.getCharacter());
                        player2 = new Player(PlayerType.HUMAN, player2.getCharacter());

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

    private void cpuTurn() {
        new Thread(() -> {
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

            Platform.runLater(this::renderBoard);
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

        // Wait on the other game.player in a new thread
        new Thread(() -> {
            try {
                // Wait for the new board
                String str;
                while ((str = inputStream.readUTF()).equals(""))
                    ;

                board = objectMapper.readValue(str, Cell[][].class);

                // Always send data from now on
                sendNetworkData = true;

                Platform.runLater(this::renderBoard);
                Platform.runLater(this::switchPlayerTurn);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void switchPlayerTurn() {
        if (checkWin(currentPlayer)) {
            status.setText("Game over - " + (currentPlayer.getCharacter().equals("b") ? "Black" : "White") + " wins");
            return;
        }

        currentPlayer = currentPlayer == player1 ? player2 : player1;

        handleTurnBegins();

        if (currentPlayer.getType() == PlayerType.CPU) {
            status.setText("CPU is thinking...");
            cpuTurn();
        } else if (currentPlayer.getType() == PlayerType.NETWORK) {
            status.setText("Waiting on game.player...");
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

    public void clearMoves() {
        activeMoves.clear();
        renderBoard();
    }

    public void handleCellClick(Cell cell) {
        if (getCurrentPlayer() != null && getCurrentPlayer().getType() == PlayerType.HUMAN) {
            String playerChar = getCurrentPlayer().getCharacter();

            if (movingCell != null && activeMoves.stream().anyMatch(move -> move.toCell == cell)) {
                Move move = activeMoves.stream().filter(m -> m.toCell == cell).findFirst().get();

                move.makeMove();
                movingCell = null;
                clearMoves();
                renderBoard();
                lastUpdate = System.currentTimeMillis();

                // Switch turn
                switchPlayerTurn();
            } else if (cell.getToken().contains(playerChar)) {
                clearMoves();
                movingCell = cell;
                activeMoves.addAll(findMoves(cell, getCurrentPlayer(), getCurrentPlayerOpponent(), noLosingMoves()));
                renderBoard();
            } else {
                clearMoves();
                movingCell = null;
            }
        }
    }

    public List<Move> getMoves(Cell[][] board, Player player, Player opponent) {
        return getMoves(board, player, opponent, false);
    }

    public List<Move> getMoves(Cell[][] board, Player player, Player opponent, boolean noLosingMoves) {
        List<Move> moveList = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].getToken().startsWith(player.getCharacter())) {
                    moveList.addAll(findMoves(board[i][j], player, opponent, noLosingMoves));
                }
            }
        }

        moveList.sort(Comparator.comparing(Move::getValue).reversed());

        return moveList;
    }

    public List<Move> findMoves(Cell cell, Player player, Player opponent, boolean noLosingMoves) {
        List<Move> moves = getValidMoves(cell, player, opponent, noLosingMoves);

        moves.forEach(move -> {
            move.setMoveHandlerFunction(c -> handleMoveMade(c));
        });
        return moves;
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

    // Test helper function
    public void setBoard(Cell[][] board) {
        this.board = board;
    }
}