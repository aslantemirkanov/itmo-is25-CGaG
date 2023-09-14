module ru.squad1337.cg {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;

    opens ru.squad1337.cg to javafx.fxml;
    exports ru.squad1337.cg;
}