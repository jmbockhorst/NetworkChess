import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import player.PlayerType;
import views.Chess;
import views.MainMenu;
import views.NetworkMenu;

public class Game extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene menuScene = MainMenu.getScene(e -> {
            Scene currentScene = primaryStage.getScene();
            Chess chess = new Chess(PlayerType.CPU);
            primaryStage.setScene(chess.getScene(ev -> primaryStage.setScene(currentScene)));
            chess.start();
        }, e -> {
            Scene currentScene = primaryStage.getScene();
            Chess chess = new Chess(PlayerType.HUMAN);
            primaryStage.setScene(chess.getScene(ev -> primaryStage.setScene(currentScene)));
            chess.start();
        }, e -> {
            Scene currentScene = primaryStage.getScene();
            NetworkMenu networkMenu = new NetworkMenu(primaryStage, ev -> primaryStage.setScene(currentScene));
            primaryStage.setScene(networkMenu.getScene());
        });

        primaryStage.setScene(menuScene);
        primaryStage.setTitle("Chess");
        primaryStage.show();
    }
}