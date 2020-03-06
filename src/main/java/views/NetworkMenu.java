package views;

import chess.NetworkGameClient;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import player.PlayerType;

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
    Stage primaryStage;

    public NetworkMenu(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;

            socket = new Socket("localhost", 8000);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            objectMapper = new ObjectMapper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        BorderPane mainPane = new BorderPane();

        // Refresh button to reload the games list
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            mainPane.setCenter(getGamesList());
        });

        Button hostGameButton = new Button("Host game");
        hostGameButton.setOnAction(e -> {
            try {
                String gameName = "Test game";

                // Send the start game request
                outputStream.writeUTF("create-" + gameName);
                outputStream.flush();

                // Create the new game with the current connection
                Chess chess = new Chess(PlayerType.NETWORK, socket);
                System.out.println("Setup chess game");
                primaryStage.setScene(chess.getScene());

                Platform.runLater(() -> chess.start());

                System.out.println("Started game");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        mainPane.setTop(refreshButton);
        mainPane.setCenter(getGamesList());
        mainPane.setBottom(hostGameButton);

        return new Scene(mainPane, 600, 600);
    }

    private VBox getGamesList() {
        List<NetworkGameClient> games = getGames();

        VBox box = new VBox();
        box.setSpacing(10.0);

        games.forEach(networkGame -> {
            HBox gameBox = new HBox();
            gameBox.setSpacing(10.0);

            Label nameLabel = new Label(networkGame.getName());
            Button joinButton = new Button("Join");

            joinButton.setOnAction(e -> {
                try {
                    // Join the game with id
                    outputStream.writeUTF("join - " + networkGame.getGameId());
                    outputStream.flush();

                    // Create the new game with the current connection
                    Chess chess = new Chess(PlayerType.NETWORK, socket);
                    primaryStage.setScene(chess.getScene());

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
}