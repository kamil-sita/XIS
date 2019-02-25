package sections;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import main.MainViewController;
import toolset.io.BufferedImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public final class UserFeedback {

    private MainViewController mainViewController;

    private static UserFeedback instance;

    public static UserFeedback getInstance() {
        if (instance == null) instance = new UserFeedback();
        return instance;
    }

    public UserFeedback() {
        this.mainViewController = MainViewController.getInstance();
    }

    public void reportProgress (double percentProgress) {
        Platform.runLater(() -> mainViewController.getProgressBar().setProgress(percentProgress));
    }

    public void reportProgress (String message) {
        Platform.runLater(() -> mainViewController.setStatus(message));
    }

    public void popup(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.CLOSE);
            alert.show();
        });

    }

    public void openInDefault(File file) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch (Exception e) {
            //
        }
    }

    public void openInDefault(BufferedImage bufferedImage) {
        var file = BufferedImageIO.saveImage(bufferedImage, null);
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void longPopupTextArea(String message) {
        Platform.runLater(() -> {
            Parent root;
            UserFeedback userFeedback;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(UserFeedback.class.getClassLoader().getResource("sections/textAreaPopup.fxml"));
                root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Message");
                stage.setScene(new Scene(root));

                userFeedback = fxmlLoader.getController();
                stage.show();
                userFeedback.textArea.setText(message);
            } catch (IOException e) {
                //
            }
        });
    }

    @FXML
    private TextArea textArea;

}
