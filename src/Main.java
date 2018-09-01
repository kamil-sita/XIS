import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sections.main.MainViewController;

public final class Main extends Application {

    private static Stage stage;

    public static void main(String[] args) {
        //new ScannerToNoteConverter().convert();
        launch(args);
    }

    @Override
    public void start(Stage stageI) throws Exception{
        stage = stageI;
        Parent root = FXMLLoader.load(getClass().getResource("sections/main/mainView.fxml"));
        stage.setTitle("XIS pre-alpha");
        stage.setScene(new Scene (root, 1280, 800));
        stage.setMinHeight(480);
        stage.setMinWidth(800);
        stage.show();

        addResizingListener((obs, oldVal, newVal) -> windowSizeChange());
    }

    public static void addResizingListener(ChangeListener<? super Number> listener) {
        stage.widthProperty().addListener(listener);
        stage.heightProperty().addListener(listener);
    }

    private void windowSizeChange() {
        MainViewController.onWindowSizeChange();
    }
}
