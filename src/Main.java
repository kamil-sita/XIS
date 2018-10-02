import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sections/main/mainView.fxml"));
        stage.setTitle("XIS pre-alpha");
        stage.setScene(new Scene (root, 1280, 800));
        stage.setMinHeight(480);
        stage.setMinWidth(800);
        stage.show();

        new StageWithListener(stage).addListeners();
    }

}
