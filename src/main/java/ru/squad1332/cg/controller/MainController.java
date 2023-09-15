package ru.squad1332.cg.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ru.squad1332.cg.controllers.PictureController;
import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;

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
                for (int h = 0; h < picture.getHeight(); h++) {
                    for (int w = 0; w < picture.getWidth(); w++) {
                        int[] rgb = picture.getPixelData()[h][w].getRgb();
                        double r = ((rgb[0] < 0) ? 256 + rgb[0] : rgb[0]) / 255.0;
                        double g = ((rgb[1] < 0) ? 256 + rgb[1] : rgb[1]) / 255.0;
                        double b = ((rgb[2] < 0) ? 256 + rgb[2] : rgb[2]) / 255.0;

                        pixelWriter.setColor(w, h, new Color(r, g, b, 1.0));
                    }
                }
            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            this.errorMessage.setText("Не удалось открыть изображение");
        }
    }

    @FXML
    protected void onSave(MouseEvent event) {
        try {
            String path = picture.getPath();
            System.out.println(path);
        } catch (Throwable e) {
            this.errorMessage.setText("Не удалось сохранить изображение");
        }
    }

    @FXML
    protected void onSaveAs(MouseEvent event) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Выберите директорию");
            File selectedDirectory = directoryChooser.showDialog(((Node) event.getSource()).getScene().getWindow());
            String path = selectedDirectory.getAbsolutePath();
            picture.setPath(path);
            picture.getFile();


        } catch (Throwable e) {
            this.errorMessage.setText("Не удалось сохранить изображение");
        }
    }

}