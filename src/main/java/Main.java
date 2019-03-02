import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.MainViewController;
import sections.GlobalSettings;
import sections.StageWithListener;

public final class Main extends Application {

    public static void main(String[] args) {
        GlobalSettings.getInstance(); //initializing global settings
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        var res = getClass().getResource("main/mainView.fxml");
        Parent root = FXMLLoader.load(res);
        stage.setTitle("XIS 0.3");
        stage.setScene(new Scene (root, 1280, 800));
        stage.setMinHeight(480);
        stage.setMinWidth(800);
        stage.show();

        new StageWithListener(stage).addListeners();

        stage.setOnHiding(event -> MainViewController.getInstance().setAppClosed(true));
    }
}
