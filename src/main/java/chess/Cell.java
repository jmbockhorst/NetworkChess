package chess;

import player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cell implements Comparable<Cell> {
    private String token;
    private int i;
    private int j;

    public Cell() {

    }

    public Cell(int i, int j) {
        this.i = i;
        this.j = j;
        this.token = "";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public List<Move> findMoves(Cell[][] board, Player player, Player opponent, boolean noLosingMoves) {
        List<Move> moves = new ArrayList<>();

        String playerChar = player.getCharacter();
        String opponentChar = opponent.getCharacter();

        // Pawn moves
        if (token.endsWith("p")) {
            // 2 space moves from the start
            if (playerChar.equals("w")) {
                if (i == 6 && board[i - 1][j].getToken().equals("")) {
                    Cell toCell = board[i - 2][j];
                    if (toCell.getToken().equals("")) {
                        moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                    }
                }
            } else if (playerChar.equals("b")) {
                if (i == 1 && board[i + 1][j].getToken().equals("")) {
                    Cell toCell = board[i + 2][j];
                    if (toCell.getToken().equals("")) {
                        moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                    }
                }
            }

            int m = 0;

            if (playerChar.equals("w")) {
                m = 1;
            } else if (playerChar.equals("b")) {
                m = -1;
            }

            // Forward move
            if (i - 1 >= 0 && i + 1 < 8) {
                Cell toCell = board[i - m][j];
                if (toCell.getToken().equals("")) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            // Diagonal attacks
            if (i - 1 >= 0 && i + 1 < 8 && j - 1 >= 0) {
                Cell toCell = board[i - m][j - 1];
                if (toCell.getToken().startsWith(opponentChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i - 1 >= 0 && i + 1 < 8 && j + 1 < 8) {
                Cell toCell = board[i - m][j + 1];
                if (toCell.getToken().startsWith(opponentChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }
        }

        // Knight moves
        if (token.endsWith("n")) {
            // Vertical moves
            if (i - 2 >= 0 && j - 1 >= 0) {
                Cell toCell = board[i - 2][j - 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i - 2 >= 0 && j + 1 < 8) {
                Cell toCell = board[i - 2][j + 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 2 < 8 && j - 1 >= 0) {
                Cell toCell = board[i + 2][j - 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 2 < 8 && j + 1 < 8) {
                Cell toCell = board[i + 2][j + 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            // Horizontal moves
            if (i - 1 >= 0 && j - 2 >= 0) {
                Cell toCell = board[i - 1][j - 2];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i - 1 >= 0 && j + 2 < 8) {
                Cell toCell = board[i - 1][j + 2];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 1 < 8 && j - 2 >= 0) {
                Cell toCell = board[i + 1][j - 2];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 1 < 8 && j + 2 < 8) {
                Cell toCell = board[i + 1][j + 2];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }
        }

        // Rook and Queen moves
        if (token.endsWith("r") || token.endsWith("q")) {
            // Vertical moves
            for (int a = i + 1; a < 8; a++) {
                Cell toCell = board[a][j];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));

                    if (toCell.getToken().startsWith(opponentChar)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            for (int a = i - 1; a >= 0; a--) {
                Cell toCell = board[a][j];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));

                    if (toCell.getToken().startsWith(opponentChar)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            // Horizontal moves
            for (int a = j + 1; a < 8; a++) {
                Cell toCell = board[i][a];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));

                    if (toCell.getToken().startsWith(opponentChar)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            for (int a = j - 1; a >= 0; a--) {
                Cell toCell = board[i][a];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));

                    if (toCell.getToken().startsWith(opponentChar)) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        // Bishop and Queen moves
        if (token.endsWith("b") || token.endsWith("q")) {
            int b;

            // Up slope moves
            b = j + 1;
            for (int a = i + 1; a < 8; a++) {
                if (b < 8) {
                    Cell toCell = board[a][b];
                    if (!toCell.getToken().startsWith(playerChar)) {
                        moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));

                        if (toCell.getToken().startsWith(opponentChar)) {
                            break;
                        }
                    } else {
                        break;
                    }

                    b++;
                }
            }

            b = j - 1;
            for (int a = i - 1; a >= 0; a--) {
                if (b >= 0) {
                    Cell toCell = board[a][b];
                    if (!toCell.getToken().startsWith(playerChar)) {
                        moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));

                        if (toCell.getToken().startsWith(opponentChar)) {
                            break;
                        }
                    } else {
                        break;
                    }

                    b--;
                }
            }

            // Down slope moves
            b = j - 1;
            for (int a = i + 1; a < 8; a++) {
                if (b >= 0) {
                    Cell toCell = board[a][b];
                    if (!toCell.getToken().startsWith(playerChar)) {
                        moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));

                        if (toCell.getToken().startsWith(opponentChar)) {
                            break;
                        }
                    } else {
                        break;
                    }

                    b--;
                }
            }

            b = j + 1;
            for (int a = i - 1; a >= 0; a--) {
                if (b < 8) {
                    Cell toCell = board[a][b];
                    if (!toCell.getToken().startsWith(playerChar)) {
                        moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));

                        if (toCell.getToken().startsWith(opponentChar)) {
                            break;
                        }
                    } else {
                        break;
                    }

                    b++;
                }
            }
        }

        if (token.endsWith("k")) {
            if (i - 1 >= 0) {
                Cell toCell = board[i - 1][j];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i - 1 >= 0 && j - 1 >= 0) {
                Cell toCell = board[i - 1][j - 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 1 < 8) {
                Cell toCell = board[i + 1][j];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 1 < 8 && j + 1 < 8) {
                Cell toCell = board[i + 1][j + 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 1 < 8 && j - 1 >= 0) {
                Cell toCell = board[i + 1][j - 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (i - 1 >= 0 && j + 1 < 8) {
                Cell toCell = board[i - 1][j + 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (j + 1 < 8) {
                Cell toCell = board[i][j + 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }

            if (j - 1 >= 0) {
                Cell toCell = board[i][j - 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(this, toCell, Piece.value(toCell.getToken(), opponentChar)));
                }
            }
        }

        if (noLosingMoves) {
            for (Iterator<Move> iterator = moves.iterator(); iterator.hasNext(); ) {
                Move move = iterator.next();

                // Check if making this move causes the other player to win
                move.makeMove();

                boolean foundWinningMove = false;
                List<Move> opponentMoves = Board.getMoves(board, opponent, player);
                for (Move oppMove : opponentMoves) {
                    if (oppMove.toCell.getToken().endsWith("k")) {
                        foundWinningMove = true;
                        System.out.println(move + " should be removed because the opponent will win with " + oppMove);
                        break;
                    }
                }

                if (foundWinningMove) {
                    iterator.remove();
                }

                move.undoMove();
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        return "Cell{" + "token='" + token + '\'' + ", i=" + i + ", j=" + j + '}';
    }

    @Override
    public int compareTo(Cell o) {
        int first = getI() - o.getI();

        if(first == 0){
            return getJ() - o.getJ();
        }

        return first;
    }
}
