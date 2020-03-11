package views;

import chess.Cell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 */
public class CellPane extends Pane {
    private String imageName;
    private Cell cell;

    public CellPane(Cell cell, CellClickedHandler cellClickedHandler) {
        setStyle("-fx-border-color: black");
        this.setPrefSize(2000, 2000);

        this.setOnMouseClicked(e -> cellClickedHandler.handleCellClicked(cell));

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

    public void refreshCell(Cell cell, boolean moving) {
        this.cell = cell;

        if (!imageName.equals(cell.getToken())) {
            setImage(cell.getToken());
        }

        if (moving) {
            setStyle("-fx-background-color: #999999");
        }
    }
}