package XIS.sections;

import XIS.main.MainViewController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XisController {
    private List<Notifier> notifiers = Collections.synchronizedList(new ArrayList<>());
    private MainViewController mainViewController;
    private static UserFeedback userFeedback;

    public XisController() {
        this.mainViewController = MainViewController.getInstance();
    }

    public void registerNotifier(Notifier notifier) {
        notifiers.add(notifier);
        mainViewController.addNotifier(notifier);
    }

    public void deregisterNotifier(Notifier notifier) {
        notifiers.remove(notifier);
        mainViewController.removeNotifier(notifier);
    }

    public void deregisterAllNotifiers() {
        mainViewController.removeAll(notifiers);
    }

    public void refreshVista() {
        mainViewController.refreshVista();
    }

    public UserFeedback getUserFeedback() {
        if (userFeedback ==  null) userFeedback = new UserFeedback();
        return userFeedback;
    }

    public MainViewController getMainViewController() {
        return mainViewController;
    }
}
