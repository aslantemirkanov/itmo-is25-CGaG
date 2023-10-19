package ru.squad1332.cg.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import ru.squad1332.cg.controllers.PictureController;
import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;

public class MainController {
    private static final double scale = 1.05;
    @FXML
    private  ScrollPane scrollPane;
    @FXML
    private ImageView firstChannel;
    @FXML
    private ImageView secondChannel;
    @FXML
    private ImageView thirdChannel;
    @FXML
    private ImageView imageView;
    @FXML
    private MenuBar menu;
    @FXML
    private Button toRed;
    @FXML
    private Button toHSL;
    @FXML
    private Label errorMessage;
    @FXML
    private Label filename;
    private File file;
    private Picture picture;
    private Mode mode;
    private Channel channel;
    private PictureController pictureController = new PictureController();
    private double zoomFactor = scale;

    @FXML
    protected void onOpen(ActionEvent event) {
        try {
            this.errorMessage.setText("");
            FileChooser fileChooser = new FileChooser();
            this.file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
            if (this.file != null) {
                this.picture = pictureController.openPicture(this.file.getPath());
                draw(picture);
            }

        } catch (Throwable e) {
            System.out.println(e.getMessage());
            this.errorMessage.setText("Не удалось открыть изображение");
        }
    }

    private void draw(Picture picture) {
        WritablePixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();
        WritableImage image = new WritableImage(picture.getWidth(), picture.getHeight());
        image.getPixelWriter().setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                format, picture.getIntArgb(Mode.RGB, Channel.ALL),
                0, picture.getWidth());
        imageView.setImage(image);
    }

    private void draw(Picture picture, Mode mode, Channel channel) {
        this.mode = mode;
        this.channel = channel;
        WritablePixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();
        WritableImage image = new WritableImage(picture.getWidth(), picture.getHeight());
        image.getPixelWriter().setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                format, picture.getIntArgb(mode, channel),
                0, picture.getWidth());
        imageView.setImage(image);
    }

    private void draw(Picture picture, Mode mode) {
        this.mode = mode;
        WritablePixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();
        WritableImage image = new WritableImage(picture.getWidth(), picture.getHeight());
        image.getPixelWriter().setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                format, picture.getIntArgb(mode, Channel.ALL),
                0, picture.getWidth());
        imageView.setImage(image);
        WritableImage imageFirst = new WritableImage(picture.getWidth(), picture.getHeight());
        imageFirst.getPixelWriter().setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                format, picture.getIntArgb(mode, Channel.FIRST),
                0, picture.getWidth());
        firstChannel.setImage(imageFirst);
        WritableImage imageSecond = new WritableImage(picture.getWidth(), picture.getHeight());
        imageSecond.getPixelWriter().setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                format, picture.getIntArgb(mode, Channel.SECOND),
                0, picture.getWidth());
        secondChannel.setImage(imageSecond);
        WritableImage imageThird = new WritableImage(picture.getWidth(), picture.getHeight());
        imageThird.getPixelWriter().setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                format, picture.getIntArgb(mode, Channel.THIRD),
                0, picture.getWidth());
        thirdChannel.setImage(imageThird);
    }

    @FXML
    protected void onSaveAs(ActionEvent event) {
        try {
            this.errorMessage.setText("");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            File selectedFile = fileChooser.showSaveDialog(imageView.getScene().getWindow());
            if (selectedFile != null) {
                picture.writeToFile(selectedFile, this.mode, this.channel);
            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            this.errorMessage.setText("Не удалось сохранить изображение");
        }
    }

    public void onToRgb() {
        draw(this.picture, Mode.RGB);
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
        draw(this.picture, Mode.HSL);
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
        draw(this.picture, Mode.HSV);
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
        draw(this.picture, Mode.YCBCR601);
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
        draw(this.picture, Mode.YCBCR709);
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
        draw(this.picture, Mode.YCOCG);
    }

    public void onY() {
        draw(this.picture, Mode.YCOCG, Channel.FIRST);
    }

    public void onCo() {
        draw(this.picture, Mode.YCOCG, Channel.SECOND);
    }

    public void onCg() {
        draw(this.picture, Mode.YCOCG, Channel.THIRD);
    }

    public void onCmy() {
        draw(this.picture, Mode.CMY);
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

    public void handleScroll(ScrollEvent event) {
        if (event.isControlDown()) {
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                zoomFactor /= scale;
            } else if (deltaY > 0) {
                zoomFactor *= scale;
            }
            imageView.setScaleX(zoomFactor);
            imageView.setScaleY(zoomFactor);

            event.consume();
        }
    }
}
