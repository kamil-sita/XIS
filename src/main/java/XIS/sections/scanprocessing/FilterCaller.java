package XIS.sections.scanprocessing;

import XIS.sections.Interruptible;
import XIS.sections.SingleJobManager;
import XIS.toolset.JavaFXTools;
import XIS.toolset.io.PdfIO;
import XIS.toolset.scanfilters.Filter;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.File;

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
                };
            }
        });
    }




    public static void multipleImages(File input, File output,
                                      int dpi,
                                      Filter filter,
                                      final JavaFXTools.SetImageDelegate imageOutput) {
        SingleJobManager.setAndRunJob(new Interruptible() {
            @Override
            public Runnable getRunnable() {
                return () -> {
                    Platform.runLater(() -> getUserFeedback().popup("Popup will show up once PDF is filtered"));
                    getUserFeedback().reportProgress("Starting...");
                    PdfFilter.filter(input, output, filter, this, imageOutput, dpi);
                };
            }

            @Override
            public Runnable onUninterruptedFinish() {
                return () -> {
                    Platform.runLater(() -> getUserFeedback().popup("Finished filtering pdf"));
                };
            }
        });
    }





    public static void previewPdf(File input, int dpi, int page,
                                  Filter filter,
                                  ImageView imageView,
                                  final JavaFXTools.SetImageDelegate imageDelegate) {

        SingleJobManager.setAndRunJob(new Interruptible() {
            BufferedImage output;
            @Override
            public Runnable getRunnable() {
                return () -> {
                    getUserFeedback().reportProgress("Converting...");
                    output = PdfIO.getPdfAsImage(input, page, dpi,this);
                    output = filter.filter(output, this);
                };
            }

            @Override
            public Runnable onUninterruptedFinish() {
                return () -> {
                    getUserFeedback().reportProgress("Converted image!");

                    Platform.runLater(() -> imageDelegate.set(output));
                };
            }
        });
    }
}
