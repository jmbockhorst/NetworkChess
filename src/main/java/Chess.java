import java.util.ArrayList;
import java.util.Comparator;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Chess extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    String player = "w";
    String opponent = "b";

    Cell[][] board = new Cell[8][8];
    boolean[][] moves = new boolean[8][8];
    Cell movingCell;
    boolean moving = false;

    Label status = new Label("Your turn");

    @Override
    public void start(Stage primaryStage) {
        GridPane pane = new GridPane();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pane.add(board[i][j] = new Cell(this, i, j), j, i);
                if (j % 2 == 0 ^ i % 2 == 0) {
                    board[i][j].setStyle("-fx-background-color: #e29b3d");
                } else {
                    board[i][j].setStyle("-fx-background-color: #f7ca8f");
                }
            }
        }

        setUpBoard();

        status.setFont(Font.font("Times New Roman", 24));

        Button resetButton = new Button("Play Again");
        resetButton.setOnMouseClicked(e -> resetGame());

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);
        borderPane.setBottom(status);
        BorderPane.setAlignment(status, Pos.CENTER);

        Scene scene = new Scene(borderPane, 600, 600);
        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.setScene(scene);
        primaryStage.show();
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

    String cpuChar = "b";
    String humanChar = "w";
    int positionCount = 0;

    private int alphaBeta(Cell[][] cells, int alpha, int beta, int depth, boolean isMax) {
        positionCount++;

        if (depth == 0) {
            return -evaluateBoard(cells);
        }

        if (isMax) {
            ArrayList<Move> maxMoveList = getMoves(cells, cpuChar);

            int bestMove = -9999;

            for (int i = 0; i < maxMoveList.size(); i++) {
                Move move = maxMoveList.get(i);
                move.makeTempMove();

                bestMove = Math.max(bestMove, alphaBeta(cells, alpha, beta, depth - 1, false));

                move.undoMove();

                alpha = Math.max(alpha, bestMove);
                if (beta <= alpha) {
                    break;
                }
            }

            return bestMove;
        } else {
            ArrayList<Move> minMoveList = getMoves(cells, humanChar);

            int bestMove = 9999;

            for (int i = 0; i < minMoveList.size(); i++) {
                Move move = minMoveList.get(i);
                move.makeTempMove();

                bestMove = Math.min(bestMove, alphaBeta(cells, alpha, beta, depth - 1, true));

                move.undoMove();

                beta = Math.min(beta, bestMove);
                if (beta <= alpha) {
                    break;
                }
            }

            return bestMove;
        }
    }

    boolean gameOver = false;

    public void cpuPlay() {
        int depth = 6;
        int alpha = -10000;
        int beta = 10000;

        positionCount = 0;

        ArrayList<Move> maxMoveList = getMoves(board, cpuChar);

        int bestMove = -9999;
        int bestInt = 0;

        for (int i = 0; i < maxMoveList.size(); i++) {
            Move move = maxMoveList.get(i);
            move.makeTempMove();

            bestMove = Math.max(bestMove, alphaBeta(board, alpha, beta, depth - 1, false));

            move.undoMove();

            if (bestMove > alpha) {
                alpha = bestMove;
                bestInt = i;
            }

            if (beta <= alpha) {
                break;
            }
        }

        if (maxMoveList.get(bestInt).toCell.getToken().endsWith("k")) {
            gameOver = true;
        }

        maxMoveList.get(bestInt).makeMove();

        System.out.println(positionCount);
    }

    public ArrayList<Move> getMoves(Cell[][] cells, String player) {
        ArrayList<Move> moveList = new ArrayList<Move>();

        String opponent = (player == "w") ? "b" : "w";

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cells[i][j].getToken().startsWith(player)) {
                    cells[i][j].findMoves(player, opponent);

                    for (int a = 0; a < 8; a++) {
                        for (int b = 0; b < 8; b++) {
                            if (moves[a][b]) {
                                moveList.add(new Move(this, cells[i][j], cells[a][b]));
                            }
                        }
                    }

                    clearMoves();
                }
            }
        }

        // Collections.sort(moveList, new SortMove());
        moveList.sort(Comparator.comparing(Move::getValue).reversed());

        return moveList;
    }

    public int evaluateBoard(Cell[][] cells) {
        int total = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                total += getPieceValue(cells[i][j]);
            }
        }

        return total;
    }

    public int getPieceValue(Cell cell) {
        int absValue = 0;
        if (cell.getToken().endsWith("p"))
            absValue = 10;
        else if (cell.getToken().endsWith("r"))
            absValue = 50;
        else if (cell.getToken().endsWith("n"))
            absValue = 30;
        else if (cell.getToken().endsWith("b"))
            absValue = 30;
        else if (cell.getToken().endsWith("q"))
            absValue = 90;
        else if (cell.getToken().endsWith("k"))
            absValue = 900;

        return (cell.getToken().startsWith(humanChar)) ? absValue : -absValue;
    }

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j].setToken("");
                setUpBoard();
                player = "w";
            }
        }
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
    }

    public void clearMoves() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                moves[i][j] = false;

                if (j % 2 == 0 ^ i % 2 == 0) {
                    board[i][j].setStyle("-fx-background-color: #e29b3d");
                } else {
                    board[i][j].setStyle("-fx-background-color: #f7ca8f");
                }
            }
        }
    }
}

class SortMove implements Comparator<Move> {
    @Override
    public int compare(Move o1, Move o2) {
        return (o1.value > o2.value) ? o1.value : o2.value;
    }
}
