package chess;

import player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Board {
    public static List<Move> getMoves(Cell[][] cells, Player player, Player opponent){
        return getMoves(cells, player, opponent, false);
    }

    public static List<Move> getMoves(Cell[][] cells, Player player, Player opponent, boolean noLosingMoves) {
        List<Move> moveList = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cells[i][j].getToken().startsWith(player.getCharacter())) {
                    moveList.addAll(cells[i][j].findMoves(cells, player, opponent, noLosingMoves));
                }
            }
        }

        moveList.sort(Comparator.comparing(Move::getValue).reversed());

        return moveList;
    }

    public static boolean isChecked(Cell[][] board, Player player1, Player player2) {
        List<Move> moves1 = Board.getMoves(board, player1, player2);
        List<Move> moves2 = Board.getMoves(board, player2, player1);

        for (Move move : moves1) {
            // Check if there is move to get the king
            if (move.toCell.getToken().endsWith("k")) {
                return true;
            }
        }

        for (Move move : moves2) {
            // Check if there is move to get the king
            if (move.toCell.getToken().endsWith("k")) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkWin(Cell[][] board, Player player) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getToken().equals(player.getCharacter() + "k")) {
                    return false;
                }
            }
        }

        return true;
    }
}
