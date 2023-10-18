package ru.squad1332.cg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);
        stage.setTitle("photo viewer!");
        stage.setScene(scene);
        //stage.setFullScreen(true);
        stage.show();
    }
//@Override
//public void start(Stage primaryStage) {
//    ScalePane scalePane = new ScalePane();
//
//    // Добавление прямоугольника в панель, который будет масштабироваться
//    Rectangle rect = new Rectangle(100, 100, 200, 200);
//    rect.setFill(Color.BLUE);
//    scalePane.getChildren().add(rect);
//
//    // Создание сцены и отображение окна
//    Scene scene = new Scene(scalePane, 800, 600);
//    primaryStage.setTitle("Scale Pane Example");
//    primaryStage.setScene(scene);
//    primaryStage.show();
//}

    public static void main(String[] args) {
        launch();
    }
}