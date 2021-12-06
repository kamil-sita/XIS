package XIS;

import XIS.main.MainViewController;
import XIS.sections.StageWithListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class WindowStart extends Application {

    public static void runWindow() {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception{
        var res = getClass().getResource("main/mainView.fxml");

        Parent root = FXMLLoader.load(res);
        stage.setTitle("XIS 0.6.1");
        stage.setScene(new Scene (root, 1280, 800));
        stage.setMinHeight(480);
        stage.setMinWidth(800);
        stage.show();

        new StageWithListener(stage).addListeners();

        stage.setOnHiding(event -> MainViewController.getInstance().setAppClosed(true));
    }
}
