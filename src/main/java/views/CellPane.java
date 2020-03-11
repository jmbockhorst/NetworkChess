package views;

import chess.Cell;
import chess.Move;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import player.PlayerType;

/**
 */
public class CellPane extends Pane {
    private Chess chess;
    private String imageName = "";
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

        if (!token.equals("")) {
            ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/" + token + ".png")));
            image.setFitWidth(75);
            image.setFitHeight(70);

            this.getChildren().add(image);
        }
    }

    public void refreshCell(Cell cell) {
        this.cell = cell;

        if (imageName != cell.getToken()) {
            setImage(cell.getToken());
        }

        // Check if this is an active move
        if (chess.movingCell != null && chess.activeMoves.stream().anyMatch(move -> move.toCell == cell)) {
            setStyle("-fx-background-color: #999999");
        }
    }

    private void handleMouseClick() {
        if (chess.getCurrentPlayer() != null && chess.getCurrentPlayer().getType() == PlayerType.HUMAN) {
            String playerChar = chess.getCurrentPlayer().getCharacter();
            String opponentChar = chess.getCurrentPlayerOpponent().getCharacter();

            if (chess.movingCell != null && chess.activeMoves.stream().anyMatch(move -> move.toCell == cell)) {
                Move move = chess.activeMoves.stream().filter(m -> m.toCell == cell).findFirst().get();

                // chess.Move chess.Cell
                move.makeMove();
                chess.movingCell = null;
                chess.clearMoves();
                chess.refreshBoard();
                chess.lastUpdate = System.currentTimeMillis();

                // Switch turn
                chess.switchPlayerTurn();
            } else if (cell.getToken().contains(playerChar)) {
                chess.clearMoves();
                chess.movingCell = cell;
                chess.activeMoves.addAll(cell.findMoves(chess.board, chess.getCurrentPlayer(), chess.getCurrentPlayerOpponent(), !chess.checkMate));
                chess.refreshBoard();
            } else {
                chess.clearMoves();
                chess.movingCell = null;
            }
        }
    }
}