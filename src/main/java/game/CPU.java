package game;

import java.util.ArrayList;
import java.util.List;

import game.player.Player;

public class CPU {

    private Cell[][] board;
    private Player cpuPlayer;
    private Player opponentPlayer;
    private PieceValueFunction pieceValueFunction;
    private GetMovesFunction getMovesFunction;

    public CPU(Cell[][] board, Player cpuPlayer, Player opponentPlayer, PieceValueFunction piece,
            GetMovesFunction getMovesFunction) {
        this.board = board;
        this.cpuPlayer = cpuPlayer;
        this.opponentPlayer = opponentPlayer;
        this.pieceValueFunction = piece;
        this.getMovesFunction = getMovesFunction;
    }

    public Move getBestMove() {
        int depth = 6;
        int alpha = -10000;
        int beta = 10000;

        List<Move> maxMoveList = getMovesFunction.call(board, cpuPlayer, opponentPlayer);
        Move best = maxMoveList.get(0);

        List<Move> tiedMoves = new ArrayList<>();

        for (Move move : maxMoveList) {
            move.makeMove();

            int moveScore = alphaBeta(board, alpha, beta, depth - 1, false);

            move.undoMove();

            if (moveScore > alpha) {
                alpha = moveScore;
                best = move;

                tiedMoves.clear();
                tiedMoves.add(move);
            } else if (moveScore == alpha && move.getValue() == tiedMoves.get(0).getValue()) {
                tiedMoves.add(move);
            }

            if (beta <= alpha) {
                break;
            }
        }

        if (tiedMoves.size() > 0) {
            best = tiedMoves.get((int) (Math.random() * tiedMoves.size()));
        }

        return best;
    }

    private int alphaBeta(Cell[][] board, int alpha, int beta, int depth, boolean isMax) {
        if (depth == 0) {
            return -evaluateBoard(board);
        }

        if (isMax) {
            List<Move> maxMoveList = getMovesFunction.call(board, cpuPlayer, opponentPlayer);

            int bestMove = -9999;

            for (Move move : maxMoveList) {
                move.makeMove();

                bestMove = Math.max(bestMove, alphaBeta(board, alpha, beta, depth - 1, false));

                move.undoMove();

                alpha = Math.max(alpha, bestMove);
                if (beta <= alpha) {
                    break;
                }
            }

            return bestMove;
        } else {
            List<Move> minMoveList = getMovesFunction.call(board, opponentPlayer, cpuPlayer);

            int bestMove = 9999;

            for (Move move : minMoveList) {
                move.makeMove();

                bestMove = Math.min(bestMove, alphaBeta(board, alpha, beta, depth - 1, true));

                move.undoMove();

                beta = Math.min(beta, bestMove);
                if (beta <= alpha) {
                    break;
                }
            }

            return bestMove;
        }
    }

    private int evaluateBoard(Cell[][] cells) {
        int total = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                total += pieceValueFunction.call(cells[i][j].getToken(), opponentPlayer.getCharacter());
            }
        }

        return total;
    }
}
