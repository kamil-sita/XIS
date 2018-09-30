package sections;

/**
 * Classes implementing this interface upon registration (in static method addResizingListener() in Main class) will
 * be called whenever window is resized to ensure fluidity of  the interface.
 */
public interface Notifier {
    void notify (double width, double height);
}
