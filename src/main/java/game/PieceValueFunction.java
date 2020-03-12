package game;

/**
 * PieceValueFunction
 */
public interface PieceValueFunction {
    int call(String token, String opponentChar);
}