module ru.squad1332.cg {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;

    opens ru.squad1332.cg to javafx.fxml;
    exports ru.squad1332.cg;
    exports ru.squad1332.cg.controller;
    opens ru.squad1332.cg.controller to javafx.fxml;
}