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

        Scene menuScene = MainMenu.getScene(e -> {
            Chess chess = new Chess(PlayerType.CPU);
            primaryStage.setScene(chess.getScene());
            chess.start();
        }, e -> {
            Chess chess = new Chess(PlayerType.HUMAN);
            primaryStage.setScene(chess.getScene());
            chess.start();
        }, e -> {
            NetworkMenu networkMenu = new NetworkMenu(primaryStage);
            primaryStage.setScene(networkMenu.getScene());
        });

        primaryStage.setScene(menuScene);
        primaryStage.setTitle("Chess");
        primaryStage.show();
    }
}