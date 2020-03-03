
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Cell
 */
public class CellPane extends Pane {
    private Chess chess;
    private String imageName = "";
    int i;
    int j;
    private Cell cell;

    public CellPane(Chess chess, Cell cell) {
        setStyle("-fx-border-color: black");
        this.setPrefSize(2000, 2000);

        this.setOnMouseClicked(e -> handleMouseClick());

        this.chess = chess;
        this.cell = cell;
    }

    public void setImage(String token) {
        imageName = token;

        this.getChildren().clear();

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

    public void refreshCell() {
        if (imageName != cell.getToken()) {
            setImage(cell.getToken());
        }

        // Check if this is an active move
        if (chess.movingCell != null && chess.activeMoves.stream().anyMatch(move -> move.toCell == cell)) {
            setStyle("-fx-background-color: #999999");
        }
    }

    private void handleMouseClick() {
        if (chess.player == Chess.humanChar) {
            if (chess.movingCell != null && chess.activeMoves.stream().anyMatch(move -> move.toCell == cell)) {
                boolean gameOver = false;

                if (cell.getToken().startsWith(chess.opponent)) {
                    if (cell.getToken().endsWith("k")) {
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

                Move move = chess.activeMoves.stream().filter(m -> m.toCell == cell).findFirst().get();

                // Move Cell
                move.makeMove();
                chess.movingCell = null;
                chess.clearMoves();
                chess.refreshBoard();

                if (!gameOver) {
                    // Switch turn
                    chess.player = (chess.player == "w") ? "b" : "w";
                    chess.opponent = (chess.opponent == "w") ? "b" : "w";

                    chess.status.setText("CPU is thinking...");
                }

                Platform.runLater(() -> chess.cpuTurn());

            } else if (cell.getToken().contains(chess.player)) {
                chess.clearMoves();
                chess.movingCell = cell;
                chess.activeMoves.addAll(cell.findMoves(chess.board, chess.player, chess.opponent));
                chess.refreshBoard();
            } else {
                chess.clearMoves();
                chess.movingCell = null;
            }
        }
    }
}