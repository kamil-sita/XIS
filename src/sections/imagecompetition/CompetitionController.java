package sections.imagecompetition;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import sections.Notifier;
import sections.NotifierFactory;
import sections.UserFeedback;
import sections.main.MainViewController;

import java.awt.image.BufferedImage;

public final class CompetitionController {

    private Notifier notifierL;
    private Notifier notifierR;

    private BufferedImage imageL;
    private BufferedImage imageR;

    @FXML
    private ImageView imageViewL;

    @FXML
    private ImageView imageViewR;

    @FXML
    void newCompetitionPress(ActionEvent event) {
        var optional = UserFeedback.getText("Compretition creator", "Enter details", "File location:");
        System.out.println(optional.get());
    }

    @FXML
    void loadPress(ActionEvent event) {

    }

    @FXML
    void savePress(ActionEvent event) {

    }


    private void reAddNotifier() {
        MainViewController.removeNotifier(notifierL);
        MainViewController.removeNotifier(notifierR);
        notifierL = NotifierFactory.scalingImageNotifier(imageL, imageViewL, 300, 100, 0.5);
        notifierR = NotifierFactory.scalingImageNotifier(imageR, imageViewR, 300, 100, 0.5);
        MainViewController.addNotifier(notifierL);
        MainViewController.addNotifier(notifierR);
    }

}
