package ru.squad1332.cg.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import ru.squad1332.cg.controllers.PictureController;
import ru.squad1332.cg.entities.Picture;

import java.io.File;

public class MainController {
    @FXML
    private Label errorMessage;
    @FXML
    private Canvas canvas;
    @FXML
    private Label filename;
    @FXML
    private Button openButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button saveAsButton;
    private File file;

    private Picture picture;
    private PictureController pictureController = new PictureController();

    @FXML
    protected void onOpen(MouseEvent event) {
        try {
            this.errorMessage.setText("");
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Pnm", "*.ppm", "*.pnm", "*.pgm");
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().setAll(extensionFilter);
            File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
            if (file != null) {
                picture = pictureController.openPicture(file.getPath());
                PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();

                canvas.setWidth(picture.getWidth());
                canvas.setHeight(picture.getHeight());
                int cnt = 1;
                pixelWriter.setPixels(0, 0,
                        picture.getWidth(), picture.getHeight(),
                        PixelFormat.getIntArgbPreInstance(), picture.getPixelData(),
                        0, picture.getWidth());

            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            this.errorMessage.setText("Не удалось открыть изображение");
        }
    }

    @FXML
    protected void onSave(MouseEvent event) {
        try {
            this.errorMessage.setText("");
            String path = picture.getPath();
            System.out.println(path);
        } catch (Throwable e) {
            this.errorMessage.setText("Не удалось сохранить изображение");
        }
    }

    @FXML
    protected void onSaveAs(MouseEvent event) {
        try {
            this.errorMessage.setText("");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            File selectedFile = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
            if (selectedFile != null) {
                picture.writeToFile(selectedFile);
            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            this.errorMessage.setText("Не удалось сохранить изображение");
        }
    }

}