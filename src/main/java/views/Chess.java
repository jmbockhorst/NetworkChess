package views;

import java.net.Socket;

import chess.CPU;
import chess.Cell;
import game.ICPU;
import player.PlayerType;

interface CellClickedHandler {
    void handleCellClicked(Cell cell);
}

public class Chess extends BoardGame {
    public final static String PLAYER1_CHAR = "w";
    public final static String PLAYER2_CHAR = "b";

    // Constructors
    public Chess(PlayerType opponentType) {
        this(opponentType, null);
    }

    public Chess(PlayerType opponentType, Socket socket) {
        super(8, 8, PLAYER1_CHAR, PLAYER2_CHAR, opponentType, socket);
        cpu1 = new CPU(board, player1, player2);
        cpu2 = new CPU(board, player2, player1);
    }

    @Override
    public void setupBoardTokens() {
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

    @Override
    public void renderCell(CellPane cellPane, Cell cell) {
        if (cell.getJ() % 2 == 0 ^ cell.getI() % 2 == 0) {
            cellPane.setStyle("-fx-background-color: #e29b3d");
        } else {
            cellPane.setStyle("-fx-background-color: #f7ca8f");
        }
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
