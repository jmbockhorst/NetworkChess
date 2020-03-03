import java.util.ArrayList;
import java.util.List;

public class Cell {
    private String token;
    private int i;
    private int j;
    private int value;

    public Cell(int i, int j) {
        this.i = i;
        this.j = j;
        setToken("");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        this.value = Piece.value(token);
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getValue() {
        return value;
    }

    public List<Move> findMoves(Cell[][] board, String player, String opponent) {
        List<Move> moves = new ArrayList<>();

        // Pawn moves
        if (token.contentEquals(player + "p")) {
            if (player == "w") {
                if (i == 6) {
                    Cell toCell = board[i - 2][j];
                    if (!toCell.getToken().startsWith(player) && !toCell.getToken().startsWith(opponent)) {
                        moves.add(new Move(this, toCell));
                    }
                }
            } else if (player == "b") {
                if (i == 1) {
                    Cell toCell = board[i + 2][j];
                    if (!toCell.getToken().startsWith(player) && !toCell.getToken().startsWith(opponent)) {
                        moves.add(new Move(this, toCell));
                    }
                }
            }

            int m = 0;

            if (player == "w") {
                m = 1;
            } else if (player == "b") {
                m = -1;
            }

            if (i - 1 >= 0 && i + 1 < 8) {
                Cell toCell = board[i - 1 * m][j];
                if (!toCell.getToken().startsWith(player) && !toCell.getToken().startsWith(opponent)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i - 1 >= 0 && i + 1 < 8 && j - 1 >= 0) {
                Cell toCell = board[i - 1 * m][j - 1];
                if (toCell.getToken().startsWith(opponent)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i - 1 >= 0 && i + 1 < 8 && j + 1 < 8) {
                Cell toCell = board[i - 1 * m][j + 1];
                if (toCell.getToken().startsWith(opponent)) {
                    moves.add(new Move(this, toCell));
                }
            }
        }

        // Knight moves
        if (token.contentEquals(player + "n")) {
            // Vertical moves
            if (i - 2 >= 0 && j - 1 >= 0) {
                Cell toCell = board[i - 2][j - 1];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i - 2 >= 0 && j + 1 < 8) {
                Cell toCell = board[i - 2][j + 1];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i + 2 < 8 && j - 1 >= 0) {
                Cell toCell = board[i + 2][j - 1];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i + 2 < 8 && j + 1 < 8) {
                Cell toCell = board[i + 2][j + 1];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            // Horizontal moves
            if (i - 1 >= 0 && j - 2 >= 0) {
                Cell toCell = board[i - 1][j - 2];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i - 1 >= 0 && j + 2 < 8) {
                Cell toCell = board[i - 1][j + 2];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i + 1 < 8 && j - 2 >= 0) {
                Cell toCell = board[i + 1][j - 2];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i + 1 < 8 && j + 2 < 8) {
                Cell toCell = board[i + 1][j + 2];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }
        }

        // Rook and Queen moves
        if (token.contentEquals(player + "r") || token.contentEquals(player + "q")) {
            // Vertical moves
            for (int a = i + 1; a < 8; a++) {
                Cell toCell = board[a][j];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));

                    if (toCell.getToken().startsWith(opponent)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            for (int a = i - 1; a >= 0; a--) {
                Cell toCell = board[a][j];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));

                    if (toCell.getToken().startsWith(opponent)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            // Horizontal moves
            for (int a = j + 1; a < 8; a++) {
                Cell toCell = board[i][a];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));

                    if (toCell.getToken().startsWith(opponent)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            for (int a = j - 1; a >= 0; a--) {
                Cell toCell = board[i][a];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));

                    if (toCell.getToken().startsWith(opponent)) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        // Biship and Queen moves
        if (token.contentEquals(player + "b") || token.contentEquals(player + "q")) {
            int b;

            // Up slope moves
            b = j + 1;
            for (int a = i + 1; a < 8; a++) {
                if (b < 8) {
                    Cell toCell = board[a][b];
                    if (!toCell.getToken().startsWith(player)) {
                        moves.add(new Move(this, toCell));

                        if (toCell.getToken().startsWith(opponent)) {
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
                    if (!toCell.getToken().startsWith(player)) {
                        moves.add(new Move(this, toCell));

                        if (toCell.getToken().startsWith(opponent)) {
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
                    if (!toCell.getToken().startsWith(player)) {
                        moves.add(new Move(this, toCell));

                        if (toCell.getToken().startsWith(opponent)) {
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
                    if (!toCell.getToken().startsWith(player)) {
                        moves.add(new Move(this, toCell));

                        if (toCell.getToken().startsWith(opponent)) {
                            break;
                        }
                    } else {
                        break;
                    }

                    b++;
                }
            }
        }

        if (token.contentEquals(player + "k")) {
            if (i - 1 >= 0) {
                Cell toCell = board[i - 1][j];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i - 1 >= 0 && j - 1 >= 0) {
                Cell toCell = board[i - 1][j - 1];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i + 1 < 8) {
                Cell toCell = board[i + 1][j];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i + 1 < 8 && j + 1 < 8) {
                Cell toCell = board[i + 1][j + 1];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i + 1 < 8 && j - 1 >= 0) {
                Cell toCell = board[i + 1][j - 1];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (i - 1 >= 0 && j + 1 < 8) {
                Cell toCell = board[i - 1][j + 1];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (j + 1 < 8) {
                Cell toCell = board[i][j + 1];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }

            if (j - 1 >= 0) {
                Cell toCell = board[i][j - 1];
                if (!toCell.getToken().startsWith(player)) {
                    moves.add(new Move(this, toCell));
                }
            }
        }

        return moves;
    }
}
