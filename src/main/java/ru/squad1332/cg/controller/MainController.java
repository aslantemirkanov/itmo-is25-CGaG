package ru.squad1332.cg.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import ru.squad1332.cg.controllers.PictureController;
import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.modes.Channel;

import java.io.File;

public class MainController {
    @FXML
    private MenuBar menu;
    @FXML
    private Button toRed;
    @FXML
    private Button toHSL;
    @FXML
    private Label errorMessage;
    @FXML
    private Canvas canvas;
    @FXML
    private Label filename;
    private File file;

    private Picture picture;
    private PictureController pictureController = new PictureController();

    @FXML
    protected void onOpen(ActionEvent event) {
        try {
            this.errorMessage.setText("");
            FileChooser fileChooser = new FileChooser();
            this.file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
            if (this.file != null) {
                picture = pictureController.openPicture(this.file.getPath());
                draw(picture, Mode.RGB, Channel.ALL);
            }

        } catch (Throwable e) {
            System.out.println(e.getMessage());
            this.errorMessage.setText("Не удалось открыть изображение");
        }
    }

    private void draw(Picture picture, Mode mode, Channel channel) {
//                canvas.setScaleX(canvas.getWidth() / picture.getWidth());
//                canvas.setScaleY(canvas.getHeight() / picture.getHeight());
        canvas.setWidth(picture.getWidth());
        canvas.setHeight(picture.getHeight());
        PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        pixelWriter.setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                PixelFormat.getIntArgbPreInstance(), picture.getIntArgb(mode, channel),
                0, picture.getWidth());
        canvas.setScaleY(0.2);
        canvas.setScaleX(0.2);

    }


    @FXML
    protected void onSaveAs(ActionEvent event) {
        try {
            this.errorMessage.setText("");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            File selectedFile = fileChooser.showSaveDialog(canvas.getScene().getWindow());
            if (selectedFile != null) {
                picture.writeToFile(selectedFile);
            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            this.errorMessage.setText("Не удалось сохранить изображение");
        }
    }

    public void onToRgb() {
        draw(this.picture, Mode.RGB, Channel.ALL);
    }

    public void onToRed() {
        draw(this.picture, Mode.RGB, Channel.FIRST);
    }

    public void onToGreen() {
        draw(this.picture, Mode.RGB, Channel.SECOND);
    }

    public void onToBlue() {
        draw(this.picture, Mode.RGB, Channel.THIRD);
    }

    public void onToHsl() {
        draw(this.picture, Mode.HSL, Channel.ALL);
    }

    public void onToHue() {
        draw(this.picture, Mode.HSL, Channel.FIRST);
    }

    public void onToSaturation() {
        draw(this.picture, Mode.HSL, Channel.SECOND);
    }

    public void onToLightness() {
        draw(this.picture, Mode.HSL, Channel.THIRD);
    }

    public void onToHsv() {
        draw(this.picture, Mode.HSV, Channel.ALL);
    }

    public void onToHsvHue() {
        draw(this.picture, Mode.HSV, Channel.FIRST);
    }

    public void onToHsvSaturation() {
        draw(this.picture, Mode.HSV, Channel.SECOND);
    }

    public void onToHsvValue() {
        draw(this.picture, Mode.HSV, Channel.THIRD);
    }

    public void onYCbCr601() {
        draw(this.picture, Mode.YCBCR601, Channel.ALL);
    }

    public void Y601() {
        draw(this.picture, Mode.YCBCR601, Channel.FIRST);
    }

    public void Cb601() {
        draw(this.picture, Mode.YCBCR601, Channel.SECOND);
    }

    public void Cr601() {
        draw(this.picture, Mode.YCBCR601, Channel.THIRD);
    }

    public void onYCbCr709() {
        draw(this.picture, Mode.YCBCR709, Channel.ALL);
    }

    public void Y709() {
        draw(this.picture, Mode.YCBCR709, Channel.FIRST);
    }

    public void Cb709() {
        draw(this.picture, Mode.YCBCR709, Channel.SECOND);

    }

    public void Cr709() {
        draw(this.picture, Mode.YCBCR709, Channel.THIRD);
    }

    public void onYCoCg() {
        draw(this.picture, Mode.YCOCG, Channel.ALL);
    }

    public void onY(){
        draw(this.picture, Mode.YCOCG, Channel.FIRST);
    }

    public void onCo() {
        draw(this.picture, Mode.YCOCG, Channel.SECOND);
    }
    public void onCg() {
        draw(this.picture, Mode.YCOCG, Channel.THIRD);
    }

    public void onCmy() {
        draw(this.picture, Mode.CMY, Channel.ALL);
    }

    public void onCmyC() {
        draw(this.picture, Mode.CMY, Channel.FIRST);
    }

    public void onCmyM() {
        draw(this.picture, Mode.CMY, Channel.SECOND);
    }

    public void onCmyY() {
        draw(this.picture, Mode.CMY, Channel.THIRD);
    }
}