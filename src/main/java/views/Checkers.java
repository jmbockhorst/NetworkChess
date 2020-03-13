package views;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import game.BoardGame;
import game.Cell;
import game.Move;
import game.player.Player;
import game.player.PlayerType;
import game.ui.CellPane;

/**
 * Checkers
 */
public class Checkers extends BoardGame {

    public Checkers(PlayerType opponentType) {
        this(opponentType, null);
    }

    public Checkers(PlayerType opponentType, Socket socket) {
        super(8, 8, "r", "w", opponentType, socket);
    }

    @Override
    public void renderCell(CellPane cellPane, Cell cell) {
        if (cell.getJ() % 2 == 0 ^ cell.getI() % 2 == 0) {
            cellPane.setStyle("-fx-background-color: #ff0000");
        } else {
            cellPane.setStyle("-fx-background-color: #000000");
        }
    }

    @Override
    public void setupBoardTokens() {
        board[0][0].setToken("wc");
        board[0][2].setToken("wc");
        board[0][4].setToken("wc");
        board[0][6].setToken("wc");
        board[1][1].setToken("wc");
        board[1][3].setToken("wc");
        board[1][5].setToken("wc");
        board[1][7].setToken("wc");
        board[2][0].setToken("wc");
        board[2][2].setToken("wc");
        board[2][4].setToken("wc");
        board[2][6].setToken("wc");

        board[7][1].setToken("rc");
        board[7][3].setToken("rc");
        board[7][5].setToken("rc");
        board[7][7].setToken("rc");
        board[6][0].setToken("rc");
        board[6][2].setToken("rc");
        board[6][4].setToken("rc");
        board[6][6].setToken("rc");
        board[5][1].setToken("rc");
        board[5][3].setToken("rc");
        board[5][5].setToken("rc");
        board[5][7].setToken("rc");
    }

    @Override
    public List<Move> findMoves(Cell cell, Player player, Player opponent, boolean noLosingMoves) {
        List<Move> moves = new ArrayList<>();

        String playerChar = player.getCharacter();
        String opponentChar = opponent.getCharacter();

        int i = cell.getI();
        int j = cell.getJ();

        int m = playerChar.equals("r") ? -1 : 1;

        if ((m == 1 && i < 7) || (m == -1 && i > 0)) {
            // Left diagonal
            if (j > 0) {
                Cell toCell = board[i + m][j - 1];
                if (toCell.getToken().equals("")) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            // Right diagonal
            if (j < 7) {
                Cell toCell = board[i + m][j + 1];
                if (toCell.getToken().equals("")) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }
        }

        if ((m == 1 && i < 6) || (m == -1 && i > 1)) {
            // Left jump diagonal
            if (j > 1) {
                Cell toCell = board[i + m * 2][j - 2];
                Cell jumpedCell = board[i + m][j - 1];
                if (toCell.getToken().equals("") && jumpedCell.getToken().startsWith(opponentChar)) {
                    moves.add(new Move(cell, toCell, Arrays.asList(jumpedCell),
                            getPieceValue(jumpedCell.getToken(), opponentChar)));
                }
            }

            // Right jump diagonal
            if (j < 6) {
                try {
                    Cell toCell = board[i + m * 2][j + 2];
                    Cell jumpedCell = board[i + m][j + 1];
                    if (toCell.getToken().equals("") && jumpedCell.getToken().startsWith(opponentChar)) {
                        moves.add(new Move(cell, toCell, Arrays.asList(jumpedCell),
                                getPieceValue(jumpedCell.getToken(), opponentChar)));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    System.out.println("i = " + i + ", j = " + (j + 2) + ", m = " + m);
                }
            }
        }

        return moves;
    }

    @Override
    public boolean checkWin(Player player) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getPieceValue(String token, String opponentChar) {
        if (token.startsWith(opponentChar)) {
            return 10;
        } else if (!token.equals("")) {
            return -10;
        }

        return 0;
    }

}