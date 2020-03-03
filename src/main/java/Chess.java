import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    CellPane[][] boardDisplay = new CellPane[8][8];
    Cell[][] board = new Cell[8][8];
    List<Move> activeMoves = new ArrayList<>();

    CellPane movingCell;
    boolean moving = false;

    static String cpuChar = "b";
    static String humanChar = "w";
    int positionCount = 0;

    boolean gameOver = false;

    Label status = new Label("Your turn");

    CPU cpu;

    @Override
    public void start(Stage primaryStage) {
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
        cpu = new CPU(cpuChar, this);

        status.setFont(Font.font("Times New Roman", 24));

        Button resetButton = new Button("Play Again");
        resetButton.setOnMouseClicked(e -> resetGame());

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);
        borderPane.setBottom(status);
        BorderPane.setAlignment(status, Pos.CENTER);

        Scene scene = new Scene(borderPane, 600, 600);
        primaryStage.setTitle("Chess");
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

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardDisplay[i][j].setImage("");
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

        refreshBoard();
    }

    public void refreshBoard(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardDisplay[i][j].refreshCell();
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
}

class SortMove implements Comparator<Move> {
    @Override
    public int compare(Move o1, Move o2) {
        return (o1.getValue() > o2.getValue()) ? o1.getValue() : o2.getValue();
    }
}
