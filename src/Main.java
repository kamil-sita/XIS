import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sections.main.MainViewController;

public class Main extends Application {


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

        stage.widthProperty().addListener((obs, oldVal, newVal) -> windowSizeChange());

        stage.heightProperty().addListener((obs, oldVal, newVal) -> windowSizeChange());

        //TODO add/fix window maximalization listener - > windows don't scale well after maximalization
        stage.maximizedProperty().addListener((ov, t, t1) -> windowSizeChange());

    }

    private void windowSizeChange() {
        MainViewController.onWindowSizeChange();
    }

}
