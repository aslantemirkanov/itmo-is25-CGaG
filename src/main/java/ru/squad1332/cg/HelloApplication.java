package ru.squad1332.cg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.services.PictureService;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //launch();
        PictureService pictureService = new PictureService();
        try {
            pictureService.openPicture("/home/alexchamp/Desktop/cg/src/main/resources/ru/squad1332/225H_RGB.ppm");
            System.out.println("!!!!!!!!!!!!!!!!!!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}