import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Game extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Chess chess = new Chess(PlayerType.NETWORK);
        Scene scene = chess.getScene();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess");
        primaryStage.show();

        chess.start();
    }
}