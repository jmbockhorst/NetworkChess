package game;

import java.util.List;

import chess.Cell;
import player.Player;

/**
 * GetMovesFunction
 */
public interface GetMovesFunction {
    List<Move> call(Cell[][] board, Player player, Player opponent);
}