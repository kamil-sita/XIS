package XIS.sections.scanprocessing;

import XIS.sections.Interruptible;
import XIS.sections.SingleJobManager;
import XIS.toolset.JavaFXTools;
import XIS.toolset.scanfilters.Filter;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class FilterCaller {
    public static void oneImage(BufferedImage input, Filter filter,
                                final JavaFXTools.SetImageDelegate setPreview,
                                final JavaFXTools.SetImageDelegate imageOutput,
                                ImageView imageView) {

        SingleJobManager.setAndRunJob(new Interruptible() {
            BufferedImage output;
            @Override
            public Runnable getRunnable() {
                return () -> {
                    getUserFeedback().reportProgress("Converting...");
                    output = filter.filter(input, this);
                };
            }

            @Override
            public Runnable onUninterruptedFinish() {
                return () -> {
                    imageOutput.set(output);
                    getUserFeedback().reportProgress("Converted image!");

                    Platform.runLater(() -> setPreview.set(output));
                    JavaFXTools.showPreview(output, imageView, setPreview, getUserFeedback());
                };
            }
        });

    }
}
