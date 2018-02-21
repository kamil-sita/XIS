package sections.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainView.fxml"));
        stage.setTitle("XIS");
        stage.setScene(new Scene (root, 1280, 800));
        stage.setMinHeight(480);
        stage.setMinWidth(800);
        stage.show();

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            ControllerMainView.windowSizeChange();
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            ControllerMainView.windowSizeChange();
        });

        //TODO add window maximalization listener -> shadow bug

    }

}
