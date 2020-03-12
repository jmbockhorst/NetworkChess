package chess;

import static org.junit.Assert.*;

import game.Move;
import org.junit.Test;
import player.Player;
import player.PlayerType;
import views.Chess;

import java.util.Arrays;
import java.util.List;

public class TestPawnMoves {

    // j
    // i  0 1 2 3 4 5 6 7
    // 0 [ | | | | | | | ] Black
    // 1 [ | | | | | | | ] |
    // 2 [ | | | | | | | ] v
    // 3 [ | | | | | | | ]
    // 4 [ | | | | | | | ]
    // 5 [ | | | | | | | ] ^
    // 6 [ | | | | | | | ] |
    // 7 [ | | | | | | | ] White

    final static Player player1 = new Player(PlayerType.HUMAN, "b");
    final static Player player2 = new Player(PlayerType.HUMAN, "w");

    @Test
    public void testPawnDiagonalAttack() {
        Cell[][] board = getEmptyBoard();

        board[3][3].setToken("bp");
        board[4][3].setToken("bp");
        board[4][2].setToken("wp");

        // Can't attack or move forward
        // Should be able to attack diagonally

        assertWhiteAndBlackMoves(board, new Cell(3, 3), new Cell[] { new Cell(4, 2) });
    }

    @Test
    public void testPawn2SpacesStart() {
        Cell[][] board = getEmptyBoard();

        board[1][3].setToken("bp");
        board[1][4].setToken("bp");
        board[4][2].setToken("wp");

        assertWhiteAndBlackMoves(board, new Cell(1, 3), new Cell[] { new Cell(2, 3), new Cell(3, 3) });
    }

    @Test
    public void testPawnCannotMove2WhenOccupied() {
        Cell[][] board = getEmptyBoard();

        board[1][3].setToken("bp");
        board[1][4].setToken("bp");
        board[3][3].setToken("wp");

        assertWhiteAndBlackMoves(board, new Cell(1, 3), new Cell[] { new Cell(2, 3) });
    }

    @Test
    public void testPawnCannotJumpOpponent() {
        Cell[][] board = getEmptyBoard();

        board[1][3].setToken("bp");
        board[1][4].setToken("bp");
        board[2][3].setToken("wp");

        assertWhiteAndBlackMoves(board, new Cell(1, 3), new Cell[] { });
    }

    @Test
    public void testCannotAttackSelf() {
        Cell[][] board = getEmptyBoard();

        board[3][3].setToken("bp");
        board[4][2].setToken("bp");
        board[4][3].setToken("bp");
        board[4][4].setToken("bp");

        assertWhiteAndBlackMoves(board, new Cell(3, 3), new Cell[] { });
    }

    @Test
    public void testCannotAttackStraight() {
        Cell[][] board = getEmptyBoard();

        board[3][3].setToken("bp");
        board[4][2].setToken("bp");
        board[4][3].setToken("wp");
        board[4][4].setToken("bp");

        assertWhiteAndBlackMoves(board, new Cell(3, 3), new Cell[] { });
    }

    @Test
    public void testCanMoveStraight() {
        Cell[][] board = getEmptyBoard();

        board[3][3].setToken("bp");

        assertWhiteAndBlackMoves(board, new Cell(3, 3), new Cell[] { new Cell(4, 3) });
    }

    private Cell[][] getEmptyBoard() {
        Cell[][] board = new Cell[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Cell(i, j);
            }
        }

        return board;
    }

    private void assertMoves(Cell[][] board, Cell cell, Cell[] cells, Player player1, Player player2) {
        Chess chess = new Chess(PlayerType.CPU);
        chess.setBoard(board);
        List<Move> moves = chess.findMoves(board[cell.getI()][cell.getJ()], player1, player2, true);

        assertEquals(cells.length, moves.size());

        Arrays.sort(cells);
        moves.sort((a, b) -> {
            int first = a.toCell.getI() - b.toCell.getI();

            if (first == 0) {
                return a.toCell.getJ() - b.toCell.getJ();
            }

            return first;
        });

        for (int i = 0; i < moves.size(); i++) {
            assertMove(moves.get(i), cells[i].getI(), cells[i].getJ());
        }
    }

    private void assertMove(Move move, int i, int j) {
        assertEquals(i, move.toCell.getI());
        assertEquals(j, move.toCell.getJ());
    }

    private void assertWhiteAndBlackMoves(Cell[][] board, Cell cell, Cell[] cells){
        assertMoves(board, cell, cells, player1, player2);
        assertMoves(getReversedBoard(board), getReversedCell(cell), getReversedCells(cells), player2, player1);
    }

    private Cell[][] getReversedBoard(Cell[][] board){
        Cell[][] newBoard = new Cell[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                newBoard[7 - i][7 - j] = getReversedCell(board[i][j]);
            }
        }

        return newBoard;
    }

    private Cell getReversedCell(Cell cell){
        Cell newCell = new Cell(7 - cell.getI(), 7 - cell.getJ());

        if(cell.getToken().startsWith("b")) {
            newCell.setToken(cell.getToken().replace("b", "w"));
        }

        if(cell.getToken().startsWith("w")) {
            newCell.setToken(cell.getToken().replace("w", "b"));
        }

        return newCell;
    }

    private Cell[] getReversedCells(Cell[] cells){
        Cell[] newCells = new Cell[cells.length];

        for(int i = 0; i < cells.length; i++){
            newCells[i] = getReversedCell(cells[i]);
        }

        return newCells;
    }
}
