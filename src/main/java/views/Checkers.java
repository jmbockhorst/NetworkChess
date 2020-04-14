package views;

import java.net.Socket;
import java.util.ArrayList;
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
        board[0][0].setToken("w");
        board[0][2].setToken("w");
        board[0][4].setToken("w");
        board[0][6].setToken("w");
        board[1][1].setToken("w");
        board[1][3].setToken("w");
        board[1][5].setToken("w");
        board[1][7].setToken("w");
        board[2][0].setToken("w");
        board[2][2].setToken("w");
        board[2][4].setToken("w");
        board[2][6].setToken("w");

        board[7][1].setToken("r");
        board[7][3].setToken("r");
        board[7][5].setToken("r");
        board[7][7].setToken("r");
        board[6][0].setToken("r");
        board[6][2].setToken("r");
        board[6][4].setToken("r");
        board[6][6].setToken("r");
        board[5][1].setToken("r");
        board[5][3].setToken("r");
        board[5][5].setToken("r");
        board[5][7].setToken("r");
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

        moves.addAll(getAttackMoves(null, cell, playerChar, opponentChar));

        return moves;
    }

    private List<Move> getAttackMoves(Move lastMove, Cell cell, String playerChar, String opponentChar) {
        List<Move> moves = new ArrayList<>();

        int i = cell.getI();
        int j = cell.getJ();

        int m = playerChar.equals("r") ? -1 : 1;

        Cell startCell = lastMove == null ? cell : lastMove.fromCell;

        if ((m == 1 && i < 6) || (m == -1 && i > 1)) {
            // Left jump diagonal
            if (j > 1) {
                Cell toCell = board[i + m * 2][j - 2];
                Cell jumpedCell = board[i + m][j - 1];
                if (toCell.getToken().equals("") && jumpedCell.getToken().startsWith(opponentChar)) {
                    handleNewAttackMove(lastMove, playerChar, opponentChar, moves, startCell, toCell, jumpedCell);
                }
            }

            // Right jump diagonal
            if (j < 6) {
                Cell toCell = board[i + m * 2][j + 2];
                Cell jumpedCell = board[i + m][j + 1];
                if (toCell.getToken().equals("") && jumpedCell.getToken().startsWith(opponentChar)) {
                    handleNewAttackMove(lastMove, playerChar, opponentChar, moves, startCell, toCell, jumpedCell);
                }
            }
        }

        return moves;
    }

    private void handleNewAttackMove(Move lastMove, String playerChar, String opponentChar, List<Move> moves,
            Cell startCell, Cell toCell, Cell jumpedCell) {
        List<Cell> jumpedCells = lastMove == null ? new ArrayList<>() : new ArrayList<>(lastMove.attackedCells);

        jumpedCells.add(jumpedCell);

        int pieceValue = lastMove == null ? 0 : lastMove.getValue();

        Move move = new Move(startCell, toCell, jumpedCells,
                pieceValue + getPieceValue(jumpedCell.getToken(), opponentChar));
        moves.add(move);

        moves.addAll(getAttackMoves(move, toCell, playerChar, opponentChar));
    }

    @Override
    public boolean checkWin(Player player, Player opponentPlayer) {
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