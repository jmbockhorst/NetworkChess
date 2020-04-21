import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import game.BoardGame;
import game.player.PlayerType;
import views.Checkers;
import views.Chess;
import views.MainMenu;
import views.NetworkMenu;

public class Game extends Application {

    private NetworkMenu networkMenu;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GameType type = GameType.CHESS;

        MainMenu.render(primaryStage, e -> {
            Scene currentScene = primaryStage.getScene();
            BoardGame game = Game.getGameInstance(type, PlayerType.CPU);
            game.render(primaryStage, ev -> primaryStage.setScene(currentScene));
            game.start();
        }, e -> {
            Scene currentScene = primaryStage.getScene();
            BoardGame game = Game.getGameInstance(type, PlayerType.HUMAN);
            game.render(primaryStage, ev -> primaryStage.setScene(currentScene));
            game.start();
        }, e -> {
            Scene currentScene = primaryStage.getScene();
            networkMenu = new NetworkMenu();
            networkMenu.render(primaryStage, ev -> primaryStage.setScene(currentScene));
        });

        primaryStage.setTitle("Chess");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        networkMenu.stop();
    }

    public static BoardGame getGameInstance(GameType gameType, PlayerType playerType) {
        switch (gameType) {
            case CHESS:
                return new Chess(playerType);

            case CHECKERS:
                return new Checkers(playerType);

            default:
                return null;
        }
    }
}