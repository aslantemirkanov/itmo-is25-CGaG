package ru.squad1332.cg.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.Pair;
import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.gamma.GammaCorrection;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.services.PictureService;

import java.io.File;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ru.squad1332.cg.entities.PicturePNM.OTHER_TO_RGB;
import static ru.squad1332.cg.entities.PicturePNM.RGB_TO_OTHER;

public class MainController {
    private static final double scale = 1.05;
    private static final Map<String, Pair<Mode, Channel>> MODE_TO_FUNC = getMapModeChannel();
    @FXML
    private ImageView firstChannel;
    @FXML
    private ImageView secondChannel;
    @FXML
    private ImageView thirdChannel;
    @FXML
    private ImageView imageView;
    @FXML
    private Label errorMessage;
    private boolean isGammaShow = false;
    private File file;
    private PictureService pictureService = new PictureService();
    private Picture picture;
    private Mode mode = Mode.RGB;
    private Channel channel = Channel.ALL;
    private double zoomFactor = scale;

    private double interpretGamma = 0.0;
    private double curGamma = 0.0;



    private static Map<String, Pair<Mode, Channel>> getMapModeChannel() {
        Map<String, Pair<Mode, Channel>> ACTION_TO_PAIR = new HashMap<>();

        ACTION_TO_PAIR.put("onToRgb", Pair.of(Mode.RGB, Channel.ALL));
        ACTION_TO_PAIR.put("onToRed", Pair.of(Mode.RGB, Channel.FIRST));
        ACTION_TO_PAIR.put("onToGreen", Pair.of(Mode.RGB, Channel.SECOND));
        ACTION_TO_PAIR.put("onToBlue", Pair.of(Mode.RGB, Channel.THIRD));

        ACTION_TO_PAIR.put("onToHsl", Pair.of(Mode.HSL, Channel.ALL));
        ACTION_TO_PAIR.put("onToHue", Pair.of(Mode.HSL, Channel.FIRST));
        ACTION_TO_PAIR.put("onToSaturation", Pair.of(Mode.HSL, Channel.SECOND));
        ACTION_TO_PAIR.put("onToLightness", Pair.of(Mode.HSL, Channel.THIRD));

        ACTION_TO_PAIR.put("onToHsv", Pair.of(Mode.HSV, Channel.ALL));
        ACTION_TO_PAIR.put("onToHsvHue", Pair.of(Mode.HSV, Channel.FIRST));
        ACTION_TO_PAIR.put("onToHsvSaturation", Pair.of(Mode.HSV, Channel.SECOND));
        ACTION_TO_PAIR.put("onToHsvValue", Pair.of(Mode.HSV, Channel.THIRD));

        ACTION_TO_PAIR.put("onYCbCr601", Pair.of(Mode.YCBCR601, Channel.ALL));
        ACTION_TO_PAIR.put("Y601", Pair.of(Mode.YCBCR601, Channel.FIRST));
        ACTION_TO_PAIR.put("Cb601", Pair.of(Mode.YCBCR601, Channel.SECOND));
        ACTION_TO_PAIR.put("Cr601", Pair.of(Mode.YCBCR601, Channel.THIRD));

        ACTION_TO_PAIR.put("onYCbCr709", Pair.of(Mode.YCBCR709, Channel.ALL));
        ACTION_TO_PAIR.put("Y709", Pair.of(Mode.YCBCR709, Channel.FIRST));
        ACTION_TO_PAIR.put("Cb709", Pair.of(Mode.YCBCR709, Channel.SECOND));
        ACTION_TO_PAIR.put("Cr709", Pair.of(Mode.YCBCR709, Channel.THIRD));

        ACTION_TO_PAIR.put("onYCoCg", Pair.of(Mode.YCOCG, Channel.ALL));
        ACTION_TO_PAIR.put("onY", Pair.of(Mode.YCOCG, Channel.FIRST));
        ACTION_TO_PAIR.put("onCo", Pair.of(Mode.YCOCG, Channel.SECOND));
        ACTION_TO_PAIR.put("onCg", Pair.of(Mode.YCOCG, Channel.THIRD));

        ACTION_TO_PAIR.put("onCmy", Pair.of(Mode.CMY, Channel.ALL));
        ACTION_TO_PAIR.put("onCmyC", Pair.of(Mode.CMY, Channel.FIRST));
        ACTION_TO_PAIR.put("onCmyM", Pair.of(Mode.CMY, Channel.SECOND));
        ACTION_TO_PAIR.put("onCmyY", Pair.of(Mode.CMY, Channel.THIRD));

        return ACTION_TO_PAIR;
    }

    @FXML
    protected void onOpen(ActionEvent event) {
        try {
            System.out.println("Open " + this.mode + " " + this.channel);
            this.clean();
            FileChooser fileChooser = new FileChooser();
            this.file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
            if (this.file != null) {
                this.picture = pictureService.openPicture(this.file.getPath(), this.mode, this.channel);
                draw(picture);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            this.errorMessage.setText("Не удалось открыть изображение");
        }
    }

    private void draw(Picture picture) {
        writeOnImageView(imageView, this.mode, this.channel);
    }

    private void draw(Picture picture, Mode mode, Channel channel) {
        if (channel.equals(Channel.ALL)) {
            draw(picture, mode);
            return;
        } else {
            firstChannel.setImage(null);
            secondChannel.setImage(null);
            thirdChannel.setImage(null);
        }
        writeOnImageView(imageView, mode, channel);
    }

    private void draw(Picture picture, Mode mode) {
        this.mode = mode;
        writeOnImageView(firstChannel, mode, Channel.FIRST);
        writeOnImageView(secondChannel, mode, Channel.SECOND);
        writeOnImageView(thirdChannel, mode, Channel.THIRD);
        writeOnImageView(imageView, mode, Channel.ALL);
    }


    private void writeOnImageView(ImageView view, Mode mode, Channel channel) {
        this.mode = mode;
        this.channel = channel;
        WritablePixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();
        WritableImage image = new WritableImage(picture.getWidth(), picture.getHeight());

/*        Picture copyPicture = new PicturePNM();
        copyPicture.setMode(this.mode);
        copyPicture.setChannel(this.channel);

        Pixel[] pixelData = picture.getPixelData();

        Pixel[] pixelsCopy = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            pixelsCopy[i] = new Pixel(pixelData[i].getFirst(),
                    pixelData[i].getSecond(),
                    pixelData[i].getThird());
        }

        pixelsCopy = GammaCorrection.convertGamma(pixelsCopy, curGamma, interpretGamma);

        copyPicture.setPixelData(pixelsCopy);*/

        image.getPixelWriter().setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                format, picture.getIntArgb(mode, channel),
                //format, copyPicture.getIntArgb(mode, channel),
                0, picture.getWidth());
        view.setImage(image);
    }

    @FXML
    protected void onSaveAs(ActionEvent event) {
        try {
            System.out.println("Save " + this.mode + " " + this.channel);
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


    @FXML
    protected void colorConvertor(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String format = menuItem.getId();

        draw(this.picture, ACTION_TO_PAIR.get(format).getLeft(), MODE_TO_FUNC.get(format).getRight());
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

    private void clean() {
        this.errorMessage.setText("");
        firstChannel.setImage(null);
        secondChannel.setImage(null);
        thirdChannel.setImage(null);
        this.curGamma = 0.0;
        this.interpretGamma = 0.0;
    }

    public void onAssignGamma(ActionEvent actionEvent) {
        showAssignGammaInputDialog(new Stage());
    }

    private void showAssignGammaInputDialog(Stage primaryStage) {
        TextInputDialog dialog = new TextInputDialog("0.0");
        dialog.setTitle("Ввод гаммы");
        dialog.setHeaderText("Введите значение гаммы (от 0.0 до 128.0):");
        dialog.setContentText("Гамма:");

        dialog.showAndWait().ifPresent(gamma -> {
            try {
                double gammaValue = Double.parseDouble(gamma);
                if (gammaValue >= 0.0 && gammaValue <= 128.0) {

                    interpretGamma = gammaValue;
                    draw(picture, mode, channel);

                    /*
                    Mode prevMode = this.mode;
                    Channel prevChannel = this.channel;
                    Pixel[] pixels = picture.getArgb(mode, channel);
                    Pixel[] pixelsBefore = new Pixel[pixels.length];
                    for (int i = 0; i < pixels.length; i++) {
                        pixelsBefore[i] = new Pixel(pixels[i].getFirst(),
                                pixels[i].getSecond(),
                                pixels[i].getThird());
                    }

                    this.picture.setPixelData(
                            GammaCorrection.convertGamma(pixels, curGamma, gammaValue));

                    draw(this.picture, prevMode, prevChannel);
                    this.mode = prevMode;
                    this.channel = prevChannel;
                    this.picture.setPixelData(pixelsBefore);*/
                } else {
                    System.out.println("Неверное значение гаммы. Значение должно быть в диапазоне от 0.0 до 128.0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка при парсинге значения гаммы.");
            }
        });
    }

    public void onConvertGamma(ActionEvent actionEvent){
        showConvertGammaInputDialog(new Stage());
    }

    private void showConvertGammaInputDialog(Stage primaryStage){
        TextInputDialog dialog = new TextInputDialog("0.0");
        dialog.setTitle("Ввод гаммы");
        dialog.setHeaderText("Введите значение гаммы (от 0.0 до 128.0):");
        dialog.setContentText("Гамма:");

        dialog.showAndWait().ifPresent(gamma -> {
            try {
                double newGamma = Double.parseDouble(gamma);
                if (newGamma >= 0.0 && newGamma <= 128.0) {

                    Mode prevMode = this.mode;
                    Channel prevChannel = this.channel;
                    Pixel[] pixels = picture.getArgb(mode, channel);

                    this.picture.setPixelData(
                            GammaCorrection.convertGamma(pixels, curGamma, newGamma));

                    this.picture.setPixelData(RGB_TO_OTHER.get(prevMode).apply(this.picture.getPixelData(), Channel.ALL));
                    this.picture.setMode(prevMode);
                    this.mode = prevMode;
                    this.channel = prevChannel;
                    this.curGamma = newGamma;

                    draw(this.picture, this.mode, this.channel);

                } else {
                    System.out.println("Неверное значение гаммы. Значение должно быть в диапазоне от 0.0 до 128.0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка при парсинге значения гаммы.");
            }
        });
    }

}