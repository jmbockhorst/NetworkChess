package views;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.BoardGame;
import game.Cell;
import game.Move;
import game.ui.CellPane;
import game.player.Player;
import game.player.PlayerType;

public class Chess extends BoardGame {
    public final static String PLAYER1_CHAR = "w";
    public final static String PLAYER2_CHAR = "b";

    protected boolean checkMate = false;

    // Constructors
    public Chess(PlayerType opponentType) {
        this(opponentType, null);
    }

    public Chess(PlayerType opponentType, Socket socket) {
        super(8, 8, PLAYER1_CHAR, PLAYER2_CHAR, opponentType, socket);
    }

    @Override
    public void setupBoardTokens() {
        board[0][0].setToken("br");
        board[0][1].setToken("bn");
        board[0][2].setToken("bb");
        board[0][3].setToken("bq");
        board[0][4].setToken("bk");
        board[0][5].setToken("bb");
        board[0][6].setToken("bn");
        board[0][7].setToken("br");

        board[7][0].setToken("wr");
        board[7][1].setToken("wn");
        board[7][2].setToken("wb");
        board[7][3].setToken("wq");
        board[7][4].setToken("wk");
        board[7][5].setToken("wb");
        board[7][6].setToken("wn");
        board[7][7].setToken("wr");

        for (int i = 0; i < 8; i++) {
            board[1][i].setToken("bp");
            board[6][i].setToken("wp");
        }
    }

    @Override
    public void renderCell(CellPane cellPane, Cell cell) {
        if (cell.getJ() % 2 == 0 ^ cell.getI() % 2 == 0) {
            cellPane.setStyle("-fx-background-color: #e29b3d");
        } else {
            cellPane.setStyle("-fx-background-color: #f7ca8f");
        }
    }

    @Override
    public void handleTurnBegins() {
        // Check for king check
        if (isChecked(currentPlayer, getCurrentPlayerOpponent())) {
            messageText.setText("Check");
        } else {
            messageText.setText("");
        }

        // Check for king checkmate
        List<Move> moves = getMoves(board, currentPlayer, getCurrentPlayerOpponent(), true);
        if (moves.size() == 0) {
            messageText.setText("Checkmate");
            checkMate = true;
        }
    }

    @Override
    public List<Move> getValidMoves(Cell cell, Player player, Player opponent, boolean noLosingMoves) {
        List<Move> moves = new ArrayList<>();

        String playerChar = player.getCharacter();
        String opponentChar = opponent.getCharacter();

        int i = cell.getI();
        int j = cell.getJ();

        // Pawn moves
        if (cell.getToken().endsWith("p")) {
            // 2 space moves from the start
            if (playerChar.equals("w")) {
                if (i == 6 && board[i - 1][j].getToken().equals("")) {
                    Cell toCell = board[i - 2][j];
                    if (toCell.getToken().equals("")) {
                        moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                    }
                }
            } else if (playerChar.equals("b")) {
                if (i == 1 && board[i + 1][j].getToken().equals("")) {
                    Cell toCell = board[i + 2][j];
                    if (toCell.getToken().equals("")) {
                        moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
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
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            // Diagonal attacks
            if (i - 1 >= 0 && i + 1 < 8 && j - 1 >= 0) {
                Cell toCell = board[i - m][j - 1];
                if (toCell.getToken().startsWith(opponentChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i - 1 >= 0 && i + 1 < 8 && j + 1 < 8) {
                Cell toCell = board[i - m][j + 1];
                if (toCell.getToken().startsWith(opponentChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }
        }

        // Knight moves
        if (cell.getToken().endsWith("n")) {
            // Vertical moves
            if (i - 2 >= 0 && j - 1 >= 0) {
                Cell toCell = board[i - 2][j - 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i - 2 >= 0 && j + 1 < 8) {
                Cell toCell = board[i - 2][j + 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 2 < 8 && j - 1 >= 0) {
                Cell toCell = board[i + 2][j - 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 2 < 8 && j + 1 < 8) {
                Cell toCell = board[i + 2][j + 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            // Horizontal moves
            if (i - 1 >= 0 && j - 2 >= 0) {
                Cell toCell = board[i - 1][j - 2];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i - 1 >= 0 && j + 2 < 8) {
                Cell toCell = board[i - 1][j + 2];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 1 < 8 && j - 2 >= 0) {
                Cell toCell = board[i + 1][j - 2];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 1 < 8 && j + 2 < 8) {
                Cell toCell = board[i + 1][j + 2];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }
        }

        // Rook and Queen moves
        if (cell.getToken().endsWith("r") || cell.getToken().endsWith("q")) {
            // Vertical moves
            for (int a = i + 1; a < 8; a++) {
                Cell toCell = board[a][j];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));

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
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));

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
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));

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
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));

                    if (toCell.getToken().startsWith(opponentChar)) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        // Bishop and Queen moves
        if (cell.getToken().endsWith("b") || cell.getToken().endsWith("q")) {
            int b;

            // Up slope moves
            b = j + 1;
            for (int a = i + 1; a < 8; a++) {
                if (b < 8) {
                    Cell toCell = board[a][b];
                    if (!toCell.getToken().startsWith(playerChar)) {
                        moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));

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
                        moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));

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
                        moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));

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
                        moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));

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

        if (cell.getToken().endsWith("k")) {
            if (i - 1 >= 0) {
                Cell toCell = board[i - 1][j];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i - 1 >= 0 && j - 1 >= 0) {
                Cell toCell = board[i - 1][j - 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 1 < 8) {
                Cell toCell = board[i + 1][j];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 1 < 8 && j + 1 < 8) {
                Cell toCell = board[i + 1][j + 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i + 1 < 8 && j - 1 >= 0) {
                Cell toCell = board[i + 1][j - 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (i - 1 >= 0 && j + 1 < 8) {
                Cell toCell = board[i - 1][j + 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (j + 1 < 8) {
                Cell toCell = board[i][j + 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }

            if (j - 1 >= 0) {
                Cell toCell = board[i][j - 1];
                if (!toCell.getToken().startsWith(playerChar)) {
                    moves.add(new Move(cell, toCell, getPieceValue(toCell.getToken(), opponentChar)));
                }
            }
        }

        if (noLosingMoves) {
            for (Iterator<Move> iterator = moves.iterator(); iterator.hasNext();) {
                Move move = iterator.next();

                // Check if making this move causes the other game.player to win
                move.makeMove();

                boolean foundWinningMove = false;
                List<Move> opponentMoves = getMoves(board, opponent, player);
                for (Move oppMove : opponentMoves) {
                    if (oppMove.toCell.getToken().endsWith("k")) {
                        foundWinningMove = true;
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

    private boolean isChecked(Player player1, Player player2) {
        List<Move> moves1 = getMoves(board, player1, player2);
        List<Move> moves2 = getMoves(board, player2, player1);

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

    @Override
    public boolean checkWin(Player player, Player opponentPlayer) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getToken().equals(opponentPlayer.getCharacter() + "k")) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean noLosingMoves() {
        return !checkMate;
    }

    @Override
    public int getPieceValue(String token, String opponentChar) {
        int absValue = 0;
        if (token.endsWith("p"))
            absValue = 10;
        else if (token.endsWith("r"))
            absValue = 50;
        else if (token.endsWith("n"))
            absValue = 30;
        else if (token.endsWith("b"))
            absValue = 30;
        else if (token.endsWith("q"))
            absValue = 90;
        else if (token.endsWith("k"))
            absValue = 900;

        return (token.startsWith(opponentChar)) ? absValue : -absValue;
    }
}
