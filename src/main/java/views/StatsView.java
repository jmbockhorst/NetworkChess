package views;

import java.net.InetAddress;
import java.net.UnknownHostException;

import database.UserStat;
import database.UserStatController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StatsView {
    UserStatController userStatController;

    public StatsView() {
        userStatController = new UserStatController();
    }

    public void render(Stage stage, EventHandler<ActionEvent> exitHandler) {
        BorderPane mainPane = new BorderPane();

        Button backButton = new Button("Back");
        backButton.setFont(Font.font(20));
        backButton.setOnAction(exitHandler);

        UserStat userStat = null;

        try {
            userStat = userStatController.get(InetAddress.getLocalHost().getHostName());

            if (userStat == null) {
                userStat = new UserStat(InetAddress.getLocalHost().getHostName(), 0, 0);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (userStat == null) {
            return;
        }

        HBox box = new HBox(10);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        VBox userIdGroup = new VBox(10);
        VBox winsGroup = new VBox(10);
        VBox lossesGroup = new VBox(10);

        userIdGroup.getChildren().add(new Label("User ID"));
        userIdGroup.getChildren().add(new Label(userStat.getUserId()));

        winsGroup.getChildren().add(new Label("Wins"));
        winsGroup.getChildren().add(new Label(String.valueOf(userStat.getWins())));

        lossesGroup.getChildren().add(new Label("Losses"));
        lossesGroup.getChildren().add(new Label(String.valueOf(userStat.getLosses())));

        box.getChildren().add(userIdGroup);
        box.getChildren().add(winsGroup);
        box.getChildren().add(lossesGroup);

        mainPane.setPadding(new Insets(10));
        mainPane.setTop(backButton);
        mainPane.setCenter(box);

        stage.setScene(new Scene(mainPane, 600, 600));
    }
}