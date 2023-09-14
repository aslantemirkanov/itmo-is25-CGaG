package ru.squad1332.cg.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ThreadLocalRandom;

public class MainController {
    @FXML
    private Label errorMessage;
    @FXML
    private Canvas canvas;
    @FXML
    private Label filename;
    @FXML
    private Button openButton;
    private File file;

    @FXML
    protected void onOpen(MouseEvent event) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.file));
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Pnm", "*.ppm");
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().setAll(extensionFilter);
            File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
            if (file != null) {
                PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
                for (int w = 0; w < canvas.getWidth(); w++) {
                    for (int h = 0; h < canvas.getHeight(); h++) {
                        int random = ThreadLocalRandom.current().nextInt(0, 3);
                        if (random == 0) {
                            pixelWriter.setColor(w, h, Color.AZURE);
                        }
                        if (random == 1) {
                            pixelWriter.setColor(w, h, Color.BLUE);
                        }
                        if (random == 2) {
                            pixelWriter.setColor(w, h, Color.GREEN);
                        }

                    }
                }
            }
        } catch (Throwable e) {
            this.errorMessage.setText("Не удалось открыть изображение");
        }
    }

    @FXML
    protected void onSave(MouseEvent event) {
        try {
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Pnm", "*.ppm");
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().setAll(extensionFilter);
            fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        } catch (Throwable e) {
            this.errorMessage.setText("Не удалось сохранить изображение");
        }
    }

}