import javafx.application.Application;
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
        MainMenu.render(primaryStage, e -> {
            Scene currentScene = primaryStage.getScene();
            Chess chess = new Chess(PlayerType.CPU);
            chess.render(primaryStage, ev -> primaryStage.setScene(currentScene));
            chess.start();
        }, e -> {
            Scene currentScene = primaryStage.getScene();
            Chess chess = new Chess(PlayerType.HUMAN);
            chess.render(primaryStage, ev -> primaryStage.setScene(currentScene));
            chess.start();
        }, e -> {
            Scene currentScene = primaryStage.getScene();
            new NetworkMenu().render(primaryStage, ev -> primaryStage.setScene(currentScene));
        });

        primaryStage.setTitle("Chess");
        primaryStage.show();
    }
}