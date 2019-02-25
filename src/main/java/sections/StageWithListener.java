package sections;

import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

public final class StageWithListener extends XisController {

    private Stage stage;

    public StageWithListener(Stage stage) {
        this.stage = stage;
    }

    public void addListeners() {
        addResizingListener((obs, oldVal, newVal) -> windowSizeChange());
        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            new Thread(() -> {
                try {
                    Thread.sleep(30); //without sleeping onWindowSizeChange() method might be called too soon and everything will be scaled to wrong size
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                windowSizeChange();
            }).start();
        });
    }

    private void addResizingListener(ChangeListener<? super Number> listener) {
        stage.widthProperty().addListener(listener);
        stage.heightProperty().addListener(listener);
    }

    private void windowSizeChange() {
        refreshVista();
    }
}
