package ru.squad1332.cg.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import ru.squad1332.cg.convertor.ColorConvertor;
import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.services.PictureService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.Pair;

public class MainController {

    private static Map<String, Pair<Mode, Channel>> getMapModeChannel(){
        Map<String, Pair<Mode, Channel>> MODE_TO_FUNC = new HashMap<>();

        MODE_TO_FUNC.put("onToRgb", Pair.of(Mode.RGB, Channel.ALL));
        MODE_TO_FUNC.put("onToRed", Pair.of(Mode.RGB, Channel.FIRST));
        MODE_TO_FUNC.put("onToGreen", Pair.of(Mode.RGB, Channel.SECOND));
        MODE_TO_FUNC.put("onToBlue", Pair.of(Mode.RGB, Channel.THIRD));

        MODE_TO_FUNC.put("onToHsl", Pair.of(Mode.HSL, Channel.ALL));
        MODE_TO_FUNC.put("onToHue", Pair.of(Mode.HSL, Channel.FIRST));
        MODE_TO_FUNC.put("onToSaturation", Pair.of(Mode.HSL, Channel.SECOND));
        MODE_TO_FUNC.put("onToLightness", Pair.of(Mode.HSL, Channel.THIRD));

        MODE_TO_FUNC.put("onToHsv", Pair.of(Mode.HSV, Channel.ALL));
        MODE_TO_FUNC.put("onToHsvHue", Pair.of(Mode.HSV, Channel.FIRST));
        MODE_TO_FUNC.put("onToHsvSaturation", Pair.of(Mode.HSV, Channel.SECOND));
        MODE_TO_FUNC.put("onToHsvValue", Pair.of(Mode.HSV, Channel.THIRD));

        MODE_TO_FUNC.put("onYCbCr601", Pair.of(Mode.YCBCR601, Channel.ALL));
        MODE_TO_FUNC.put("Y601", Pair.of(Mode.YCBCR601, Channel.FIRST));
        MODE_TO_FUNC.put("Cb601", Pair.of(Mode.YCBCR601, Channel.SECOND));
        MODE_TO_FUNC.put("Cr601", Pair.of(Mode.YCBCR601, Channel.THIRD));

        MODE_TO_FUNC.put("onYCbCr709", Pair.of(Mode.YCBCR709, Channel.ALL));
        MODE_TO_FUNC.put("Y709", Pair.of(Mode.YCBCR709, Channel.FIRST));
        MODE_TO_FUNC.put("Cb709", Pair.of(Mode.YCBCR709, Channel.SECOND));
        MODE_TO_FUNC.put("Cr709", Pair.of(Mode.YCBCR709, Channel.THIRD));

        MODE_TO_FUNC.put("onYCoCg", Pair.of(Mode.YCOCG, Channel.ALL));
        MODE_TO_FUNC.put("onY", Pair.of(Mode.YCOCG, Channel.FIRST));
        MODE_TO_FUNC.put("onCo", Pair.of(Mode.YCOCG, Channel.SECOND));
        MODE_TO_FUNC.put("onCg", Pair.of(Mode.YCOCG, Channel.THIRD));

        MODE_TO_FUNC.put("onCmy", Pair.of(Mode.CMY, Channel.ALL));
        MODE_TO_FUNC.put("onCmyC", Pair.of(Mode.CMY, Channel.FIRST));
        MODE_TO_FUNC.put("onCmyM", Pair.of(Mode.CMY, Channel.SECOND));
        MODE_TO_FUNC.put("onCmyY", Pair.of(Mode.CMY, Channel.THIRD));

        return MODE_TO_FUNC;
    }
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

    private PictureService pictureService = new PictureService();

    private Picture picture;

    private Mode currentMode = Mode.RGB;

    @FXML
    protected void onOpen(ActionEvent event) {
        try {
            this.errorMessage.setText("");
            FileChooser fileChooser = new FileChooser();
            this.file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
            if (this.file != null) {
                picture = pictureService.openPicture(this.file.getPath(), currentMode);
                draw(picture, currentMode, Channel.ALL);
            }

        } catch (Throwable e) {
            System.out.println(e.getMessage());
            this.errorMessage.setText("Не удалось открыть изображение");
        }
    }

    private void draw(Picture picture, Mode mode, Channel channel) {
                //canvas.setScaleX(canvas.getWidth() / picture.getWidth());
                //canvas.setScaleY(canvas.getHeight() / picture.getHeight());
        canvas.setWidth(picture.getWidth());
        canvas.setHeight(picture.getHeight());
        PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();

        pixelWriter.setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                PixelFormat.getIntArgbPreInstance(),
                picture.getIntArgb(mode, channel),
                0, picture.getWidth());
        canvas.setScaleY(0.2);
        canvas.setScaleX(0.2);

    }


    @FXML
    protected void onSaveAs(ActionEvent event) {
/*        try {
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
        }*/
    }


    @FXML
    protected void colorConvertor(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String format = menuItem.getId();
        Map<String, Pair<Mode, Channel>> MODE_TO_FUNC = getMapModeChannel();
        draw(this.picture, MODE_TO_FUNC.get(format).getLeft(), MODE_TO_FUNC.get(format).getRight());
    }
}