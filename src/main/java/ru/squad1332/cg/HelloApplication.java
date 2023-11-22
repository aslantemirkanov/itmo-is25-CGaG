package ru.squad1332.cg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
        Parent root = fxmlLoader.load();

        Screen screen = Screen.getPrimary();
        javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();

        // Устанавливаем размеры сцены такими же, как размеры экрана
        Scene scene = new Scene(root, 1020,800);
        //Scene scene = new Scene(fxmlLoader.load(), 1620, 800);
        stage.setTitle("photo viewer!");
        stage.setScene(scene);
        //stage.setFullScreen(true);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}