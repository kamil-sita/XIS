module XIS {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires com.google.gson;

    requires java.desktop;
    requires java.sql;

    requires simple.kmeans;

    requires imgscalr.lib;
    requires org.apache.pdfbox;

    exports XIS;
    exports XIS.main;
    exports XIS.sections;

    opens XIS.sections.defaultpage to javafx.fxml;
    opens XIS.sections.compression to javafx.fxml;
    opens XIS.sections.imagecopyfinder to javafx.fxml;
    opens XIS.sections.imagecopyfinder.view1settings to javafx.fxml;
    opens XIS.sections.imagecopyfinder.view2comparison to javafx.fxml;
    opens XIS.sections.imagecopyfinder.imageinfoview to javafx.fxml;
    opens XIS.sections.scanprocessing to javafx.fxml;
    opens XIS.sections to javafx.fxml, com.google.gson;
    opens XIS.main to javafx.fxml;
}