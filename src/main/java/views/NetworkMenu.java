package views;

import game.network.NetworkGameClient;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import game.player.PlayerType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetworkMenu {

    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    ObjectMapper objectMapper;

    public NetworkMenu() {
        try {
            socket = new Socket("localhost", 8000);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            objectMapper = new ObjectMapper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(Stage stage, EventHandler<ActionEvent> exitHandler) {
        BorderPane mainPane = new BorderPane();

        Button backButton = new Button("Back");
        backButton.setFont(Font.font(20));
        backButton.setOnAction(exitHandler);

        // Refresh button to reload the games list
        Button refreshButton = new Button("Refresh");
        refreshButton.setFont(Font.font(30));
        refreshButton.setOnAction(e -> {
            mainPane.setCenter(getGamesList(stage, exitHandler));
        });

        Button hostGameButton = new Button("Host game");
        hostGameButton.setFont(Font.font(30));
        hostGameButton.setOnAction(e -> {
            try {
                String gameName = "Test game";

                // Send the start game request
                outputStream.writeUTF("create-" + gameName);
                outputStream.flush();

                // Create the new game with the current connection
                Chess chess = new Chess(PlayerType.NETWORK, socket);
                System.out.println("Setup chess game");
                chess.render(stage, exitHandler);

                Platform.runLater(() -> chess.start());

                System.out.println("Started game");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        HBox buttonGroup = new HBox();
        buttonGroup.setAlignment(Pos.BOTTOM_CENTER);
        buttonGroup.setSpacing(20);
        buttonGroup.getChildren().add(hostGameButton);
        buttonGroup.getChildren().add(refreshButton);

        mainPane.setPadding(new Insets(10));
        mainPane.setTop(backButton);
        mainPane.setCenter(getGamesList(stage, exitHandler));
        mainPane.setBottom(buttonGroup);

        stage.setScene(new Scene(mainPane, 600, 600));
    }

    private VBox getGamesList(Stage stage, EventHandler<ActionEvent> exitHandler) {
        List<NetworkGameClient> games = getGames();

        VBox box = new VBox();
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);
        box.setSpacing(10.0);

        games.forEach(networkGame -> {
            HBox gameBox = new HBox();
            gameBox.setAlignment(Pos.CENTER);
            gameBox.setSpacing(10.0);

            Label nameLabel = new Label(networkGame.getName());
            nameLabel.setFont(Font.font(30));

            Button joinButton = new Button("Join");
            joinButton.setFont(Font.font(30));

            joinButton.setOnAction(e -> {
                try {
                    // Join the game with id
                    outputStream.writeUTF("join - " + networkGame.getGameId());
                    outputStream.flush();

                    // Create the new game with the current connection
                    Chess chess = new Chess(PlayerType.NETWORK, socket);
                    chess.render(stage, exitHandler);

                    chess.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            gameBox.getChildren().add(nameLabel);
            gameBox.getChildren().add(joinButton);

            box.getChildren().add(gameBox);
        });

        return box;
    }

    private List<NetworkGameClient> getGames() {
        List<NetworkGameClient> games = new ArrayList<>();

        try {
            // Request games from the server
            outputStream.writeUTF("listGames");
            outputStream.flush();

            System.out.println("Waiting for games list");

            // Wait for the response list of games
            String str;
            while ((str = inputStream.readUTF()).equals(""))
                ;

            System.out.println("Received games list");

            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, NetworkGameClient.class);
            games = objectMapper.readValue(str, type);
        } catch (Exception e) {
            e.printStackTrace();

            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return games;
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
