package sections.defaultpage;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import sections.UserFeedback;
import sections.XisController;

import java.net.HttpURLConnection;
import java.net.URL;

public final class WelcomePageController extends XisController {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {
        new Thread(() -> {
            try {
                URL url = new URL("http://kamil-sita.github.io/XIS/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                WebEngine engine = webView.getEngine();
                Platform.runLater(() -> engine.load(url.toString()));
            } catch (Exception e) {
                UserFeedback.getInstance().reportProgress("Could not load webpage");
            }
        }).start();
        registerNotifier((width, height) -> {
           webView.setPrefHeight(height - 100);
           webView.setMinHeight(height - 100);
        });
    }
}
