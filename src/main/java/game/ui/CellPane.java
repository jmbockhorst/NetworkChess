package game.ui;

import game.Cell;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 */
public class CellPane extends Pane {
    private String imageName = "";
    private Cell cell;
    private boolean debug = false;

    public CellPane(Cell cell, CellClickedHandler cellClickedHandler) {
        setStyle("-fx-border-color: black");
        this.setPrefSize(2000, 2000);

        this.cell = cell;

        this.setOnMouseClicked(e -> cellClickedHandler.handleCellClicked(this.cell));
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

    private void drawDebugLabels() {
        Text text = new Text(cell.getI() + ", " + cell.getJ());
        text.setFill(Color.WHITE);
        this.getChildren().add(text);

        this.positionInArea(text, 0, 0, 100, 50, 0, HPos.LEFT, VPos.TOP);
    }

    public void refreshCell(Cell cell, boolean moving) {
        this.cell = cell;

        if (!imageName.equals(cell.getToken())) {
            setImage(cell.getToken());
        }

        if (debug) {
            drawDebugLabels();
        }

        if (moving) {
            setStyle("-fx-background-color: #999999");
        }
    }
}