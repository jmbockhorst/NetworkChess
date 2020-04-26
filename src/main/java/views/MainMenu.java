package views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainMenu {

    public static void render(Stage stage, EventHandler<ActionEvent> singlePlayerHandler,
            EventHandler<ActionEvent> multiPlayerHandler, EventHandler<ActionEvent> networkGameHandler,
            EventHandler<ActionEvent> statsPageHandler) {
        BorderPane mainPane = new BorderPane();

        VBox gameMenu = new VBox();

        Button singlePlayer = new Button("Singleplayer");
        styleMenuButton(singlePlayer);
        singlePlayer.setOnAction(singlePlayerHandler);

        Button multiPlayer = new Button("Multiplayer");
        styleMenuButton(multiPlayer);
        multiPlayer.setOnAction(multiPlayerHandler);

        Button networkGame = new Button("Network game");
        styleMenuButton(networkGame);
        networkGame.setOnAction(networkGameHandler);

        gameMenu.getChildren().add(singlePlayer);
        gameMenu.getChildren().add(multiPlayer);
        gameMenu.getChildren().add(networkGame);
        gameMenu.setAlignment(Pos.CENTER);
        gameMenu.setSpacing(20.0);

        mainPane.setCenter(gameMenu);

        Label title = new Label("Chess game");
        title.setFont(Font.font(50.0));

        StackPane pane = new StackPane(title);
        pane.setAlignment(Pos.CENTER);
        mainPane.setTop(pane);

        Button statsButton = new Button("Stats");
        statsButton.setOnAction(statsPageHandler);
        statsButton.setFont(Font.font(20));

        mainPane.setBottom(statsButton);
        BorderPane.setAlignment(statsButton, Pos.CENTER);

        stage.setScene(new Scene(mainPane, 600, 600));
    }

    private static void styleMenuButton(Button button) {
        button.setFont(Font.font(35));
        button.setPadding(new Insets(20, 20, 20, 20));
    }

}
