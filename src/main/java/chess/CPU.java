package chess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import player.Player;

public class CPU {

    private Cell[][] board;
    private Player cpuPlayer;
    private Player opponentPlayer;

    public CPU(Cell[][] board, Player cpuPlayer, Player opponentPlayer) {
        this.board = board;
        this.cpuPlayer = cpuPlayer;
        this.opponentPlayer = opponentPlayer;
    }

    public Move getBestMove() {
        int depth = 6;
        int alpha = -10000;
        int beta = 10000;

        List<Move> maxMoveList = getMoves(board, cpuPlayer, opponentPlayer);

        Move best = maxMoveList.get(0);

        List<Move> tiedMoves = new ArrayList<>();

        for (int i = 0; i < maxMoveList.size(); i++) {
            Move move = maxMoveList.get(i);
            move.makeMove();

            int moveScore = alphaBeta(board, alpha, beta, depth - 1, false);

            move.undoMove();

            if (moveScore > alpha) {
                alpha = moveScore;
                best = move;

                tiedMoves.clear();
            } else if (moveScore == alpha) {
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

    private int alphaBeta(Cell[][] cells, int alpha, int beta, int depth, boolean isMax) {
        if (depth == 0) {
            return -evaluateBoard(cells);
        }

        if (isMax) {
            ArrayList<Move> maxMoveList = getMoves(cells, cpuPlayer, opponentPlayer);

            int bestMove = -9999;

            for (int i = 0; i < maxMoveList.size(); i++) {
                Move move = maxMoveList.get(i);
                move.makeMove();

                bestMove = Math.max(bestMove, alphaBeta(cells, alpha, beta, depth - 1, false));

                move.undoMove();

                alpha = Math.max(alpha, bestMove);
                if (beta <= alpha) {
                    break;
                }
            }

            return bestMove;
        } else {
            ArrayList<Move> minMoveList = getMoves(cells, opponentPlayer, cpuPlayer);

            int bestMove = 9999;

            for (int i = 0; i < minMoveList.size(); i++) {
                Move move = minMoveList.get(i);
                move.makeMove();

                bestMove = Math.min(bestMove, alphaBeta(cells, alpha, beta, depth - 1, true));

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
                total += Piece.value(cells[i][j].getToken(), opponentPlayer.getCharacter());
            }
        }

        return total;
    }

    private ArrayList<Move> getMoves(Cell[][] cells, Player player, Player opponent) {
        ArrayList<Move> moveList = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cells[i][j].getToken().startsWith(player.getCharacter())) {
                    moveList.addAll(cells[i][j].findMoves(cells, player.getCharacter(), opponent.getCharacter()));
                }
            }
        }

        moveList.sort(Comparator.comparing(Move::getValue).reversed());

        return moveList;
    }
}
