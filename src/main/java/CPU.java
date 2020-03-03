import java.util.ArrayList;
import java.util.Comparator;

public class CPU {

    private String cpuChar;
    private Chess chess;

    public CPU(String cpuChar, Chess chess) {
        this.cpuChar = cpuChar;
        this.chess = chess;
    }

    public Move getBestMove() {
        int depth = 6;
        int alpha = -10000;
        int beta = 10000;

        ArrayList<Move> maxMoveList = getMoves(chess.board, cpuChar);

        int bestMove = -9999;
        int bestInt = 0;

        for (int i = 0; i < maxMoveList.size(); i++) {
            Move move = maxMoveList.get(i);
            move.makeMove();

            bestMove = Math.max(bestMove, alphaBeta(chess.board, alpha, beta, depth - 1, false));

            move.undoMove();

            if (bestMove > alpha) {
                alpha = bestMove;
                bestInt = i;
            }

            if (beta <= alpha) {
                break;
            }
        }

        if (maxMoveList.get(bestInt).toCell.getToken().endsWith("k")) {
            chess.gameOver = true;
        }

        return maxMoveList.get(bestInt);
    }

    private int alphaBeta(Cell[][] cells, int alpha, int beta, int depth, boolean isMax) {
        if (depth == 0) {
            return -evaluateBoard(cells);
        }

        if (isMax) {
            ArrayList<Move> maxMoveList = getMoves(cells, cpuChar);

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
            ArrayList<Move> minMoveList = getMoves(cells, chess.humanChar);

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
                total += Piece.value(cells[i][j].getToken());
            }
        }

        return total;
    }

    private ArrayList<Move> getMoves(Cell[][] cells, String player) {
        ArrayList<Move> moveList = new ArrayList<>();

        String opponent = (player == "w") ? "b" : "w";

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cells[i][j].getToken().startsWith(player)) {
                    moveList.addAll(cells[i][j].findMoves(cells, player, opponent));
                }
            }
        }

        moveList.sort(Comparator.comparing(Move::getValue).reversed());

        return moveList;
    }
}
