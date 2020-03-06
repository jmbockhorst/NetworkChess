package views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainMenu {

    public static Scene getScene(EventHandler<ActionEvent> singlePlayerHandler, EventHandler<ActionEvent> multiPlayerHandler,
            EventHandler<ActionEvent> networkGameHandler) {
        BorderPane mainPane = new BorderPane();

        VBox gameMenu = new VBox();

        Button singlePlayer = new Button("Singleplayer");
        singlePlayer.setOnAction(singlePlayerHandler);

        Button multiPlayer = new Button("Multiplayer");
        multiPlayer.setOnAction(multiPlayerHandler);

        Button networkGame = new Button("Network game");
        networkGame.setOnAction(networkGameHandler);

        gameMenu.getChildren().add(singlePlayer);
        gameMenu.getChildren().add(multiPlayer);
        gameMenu.getChildren().add(networkGame);

        mainPane.setCenter(gameMenu);

        return new Scene(mainPane, 600, 600);
    }

}
