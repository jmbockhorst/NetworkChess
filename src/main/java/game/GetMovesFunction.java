package game;

import java.util.List;

import game.player.Player;

/**
 * GetMovesFunction
 */
public interface GetMovesFunction {
    List<Move> call(Cell[][] board, Player player, Player opponent);
}