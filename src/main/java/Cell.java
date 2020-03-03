
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Cell
 */
public class Cell extends Pane {
    private String token = "";
    private Chess chess;
    private int i;
    private int j;

    public Cell(Chess chess, int i, int j) {
        setStyle("-fx-border-color: black");
        this.setPrefSize(2000, 2000);

        this.setOnMouseClicked(e -> handleMouseClick());
        this.setOnMouseMoved(e -> mouseReleased());

        this.chess = chess;
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String c) {
        token = c;

        this.getChildren().clear();
        // Label text = new Label(token);
        // this.getChildren().add(text);

        if (token != "") {
            this.getChildren().clear();

            ImageView image = new ImageView(new Image(getClass().getResourceAsStream(token + ".png")));
            image.setFitWidth(75);
            image.setFitHeight(70);

            this.getChildren().add(image);
        } else {
            this.getChildren().clear();
        }
    }

    public void setTokenText(String c) {
        token = c;
    }

    private void handleMouseClick() {
        if (chess.player == "w") {
            if (chess.moving) {
                if (chess.moves[i][j]) {
                    boolean gameOver = false;

                    if (chess.board[i][j].getToken().startsWith(chess.opponent)) {
                        if (chess.board[i][j].getToken().endsWith("k")) {
                            if (chess.player == "w") {
                                chess.status.setText("GAME OVER! White wins");
                            } else if (chess.player == "b") {
                                chess.status.setText("GAME OVER! Black wins");
                            }

                            chess.player = "";
                            chess.opponent = "";
                            gameOver = true;
                        }
                    }

                    // Move chess.player
                    chess.board[i][j].setToken("");
                    chess.board[i][j].setToken(chess.movingCell.getToken());
                    chess.movingCell.setToken("");
                    chess.movingCell = null;
                    chess.moving = false;
                    chess.clearMoves();

                    if (!gameOver) {
                        // Switch turn
                        chess.player = (chess.player == "w") ? "b" : "w";
                        chess.opponent = (chess.opponent == "w") ? "b" : "w";

                        chess.status.setText("CPU is thinking...");

                        // if(chess.player == "w"){
                        // chess.status.setText("White's turn");
                        // } else if (chess.player == "b"){
                        // chess.status.setText("Black's turn");
                        // }
                    }
                } else if (chess.board[i][j].getToken().contains(chess.player)) {
                    chess.clearMoves();
                    findMoves(chess.player, chess.opponent);
                }
            } else {
                findMoves(chess.player, chess.opponent);
                chess.moving = true;
            }
        }
    }

    private void mouseReleased() {
        if (chess.player == "b") {
            chess.cpu.makeBestMove();

            if (chess.gameOver) {
                chess.status.setText("GAME OVER! Black wins");
                chess.player = "";
            } else {

                chess.player = (chess.player == "w") ? "b" : "w";
                chess.opponent = (chess.opponent == "w") ? "b" : "w";

                chess.status.setText("Your turn");
            }
        }
    }

    public void findMoves(String player, String opponent) {
        // Pawn moves
        if (token.contentEquals(player + "p")) {
            if (player == "w") {
                if (i == 6) {
                    if (!chess.board[i - 2][j].getToken().startsWith(player)
                            && !chess.board[i - 2][j].getToken().startsWith(opponent)) {
                        chess.moves[i - 2][j] = true;
                    }
                }
            } else if (player == "b") {
                if (i == 1) {
                    if (!chess.board[i + 2][j].getToken().startsWith(player)
                            && !chess.board[i + 2][j].getToken().startsWith(opponent)) {
                        chess.moves[i + 2][j] = true;
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
                if (!chess.board[i - 1 * m][j].getToken().startsWith(player)
                        && !chess.board[i - 1 * m][j].getToken().startsWith(opponent)) {
                    chess.moves[i - 1 * m][j] = true;
                }
            }

            if (i - 1 >= 0 && i + 1 < 8 && j - 1 >= 0) {
                if (chess.board[i - 1 * m][j - 1].getToken().startsWith(opponent)) {
                    chess.moves[i - 1 * m][j - 1] = true;
                }
            }

            if (i - 1 >= 0 && i + 1 < 8 && j + 1 < 8) {
                if (chess.board[i - 1 * m][j + 1].getToken().startsWith(opponent)) {
                    chess.moves[i - 1 * m][j + 1] = true;
                }
            }
        }

        // Knight moves
        if (token.contentEquals(player + "n")) {
            // Vertical moves
            if (i - 2 >= 0 && j - 1 >= 0) {
                if (!chess.board[i - 2][j - 1].getToken().startsWith(player)) {
                    chess.moves[i - 2][j - 1] = true;
                }
            }

            if (i - 2 >= 0 && j + 1 < 8) {
                if (!chess.board[i - 2][j + 1].getToken().startsWith(player)) {
                    chess.moves[i - 2][j + 1] = true;
                }
            }

            if (i + 2 < 8 && j - 1 >= 0) {
                if (!chess.board[i + 2][j - 1].getToken().startsWith(player)) {
                    chess.moves[i + 2][j - 1] = true;
                }
            }

            if (i + 2 < 8 && j + 1 < 8) {
                if (!chess.board[i + 2][j + 1].getToken().startsWith(player)) {
                    chess.moves[i + 2][j + 1] = true;
                }
            }

            // Horizontal moves
            if (i - 1 >= 0 && j - 2 >= 0) {
                if (!chess.board[i - 1][j - 2].getToken().startsWith(player)) {
                    chess.moves[i - 1][j - 2] = true;
                }
            }

            if (i - 1 >= 0 && j + 2 < 8) {
                if (!chess.board[i - 1][j + 2].getToken().startsWith(player)) {
                    chess.moves[i - 1][j + 2] = true;
                }
            }

            if (i + 1 < 8 && j - 2 >= 0) {
                if (!chess.board[i + 1][j - 2].getToken().startsWith(player)) {
                    chess.moves[i + 1][j - 2] = true;
                }
            }

            if (i + 1 < 8 && j + 2 < 8) {
                if (!chess.board[i + 1][j + 2].getToken().startsWith(player)) {
                    chess.moves[i + 1][j + 2] = true;
                }
            }
        }

        // Rook and Queen moves
        if (token.contentEquals(player + "r") || token.contentEquals(player + "q")) {
            // Vertical moves
            for (int a = i + 1; a < 8; a++) {
                if (!chess.board[a][j].getToken().startsWith(player)) {
                    chess.moves[a][j] = true;

                    if (chess.board[a][j].getToken().startsWith(opponent)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            for (int a = i - 1; a >= 0; a--) {
                if (!chess.board[a][j].getToken().startsWith(player)) {
                    chess.moves[a][j] = true;

                    if (chess.board[a][j].getToken().startsWith(opponent)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            // Horizontal moves
            for (int a = j + 1; a < 8; a++) {
                if (!chess.board[i][a].getToken().startsWith(player)) {
                    chess.moves[i][a] = true;

                    if (chess.board[i][a].getToken().startsWith(opponent)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            for (int a = j - 1; a >= 0; a--) {
                if (!chess.board[i][a].getToken().startsWith(player)) {
                    chess.moves[i][a] = true;

                    if (chess.board[i][a].getToken().startsWith(opponent)) {
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
                    if (!chess.board[a][b].getToken().startsWith(player)) {
                        chess.moves[a][b] = true;

                        if (chess.board[a][b].getToken().startsWith(opponent)) {
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
                    if (!chess.board[a][b].getToken().startsWith(player)) {
                        chess.moves[a][b] = true;

                        if (chess.board[a][b].getToken().startsWith(opponent)) {
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
                    if (!chess.board[a][b].getToken().startsWith(player)) {
                        chess.moves[a][b] = true;

                        if (chess.board[a][b].getToken().startsWith(opponent)) {
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
                    if (!chess.board[a][b].getToken().startsWith(player)) {
                        chess.moves[a][b] = true;

                        if (chess.board[a][b].getToken().startsWith(opponent)) {
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
                if (!chess.board[i - 1][j].getToken().startsWith(player)) {
                    chess.moves[i - 1][j] = true;
                }
            }

            if (i - 1 >= 0 && j - 1 >= 0) {
                if (!chess.board[i - 1][j - 1].getToken().startsWith(player)) {
                    chess.moves[i - 1][j - 1] = true;
                }
            }

            if (i + 1 < 8) {
                if (!chess.board[i + 1][j].getToken().startsWith(player)) {
                    chess.moves[i + 1][j] = true;
                }
            }

            if (i + 1 < 8 && j + 1 < 8) {
                if (!chess.board[i + 1][j + 1].getToken().startsWith(player)) {
                    chess.moves[i + 1][j + 1] = true;
                }
            }

            if (i + 1 < 8 && j - 1 >= 0) {
                if (!chess.board[i + 1][j - 1].getToken().startsWith(player)) {
                    chess.moves[i + 1][j - 1] = true;
                }
            }

            if (i - 1 >= 0 && j + 1 < 8) {
                if (!chess.board[i - 1][j + 1].getToken().startsWith(player)) {
                    chess.moves[i - 1][j + 1] = true;
                }
            }

            if (j + 1 < 8) {
                if (!chess.board[i][j + 1].getToken().startsWith(player)) {
                    chess.moves[i][j + 1] = true;
                }
            }

            if (j - 1 >= 0) {
                if (!chess.board[i][j - 1].getToken().startsWith(player)) {
                    chess.moves[i][j - 1] = true;
                }
            }
        }

        // Fill in moves with gray area
        if (player == player) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (chess.moves[i][j]) {
                        chess.board[i][j].setStyle("-fx-background-color: #999999");
                    }
                }
            }

            chess.movingCell = this;
        }
    }
}