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
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.services.PictureService;

import java.io.File;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

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
    private double curGamma = 1.0;

    private static Map<String, Pair<Mode, Channel>> getMapModeChannel() {
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
        this.mode = mode;
        this.channel = channel;
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

    private void convertGamma(Picture picture, double newGamma) {

    }

    private void assignGamma(double gamma) {
        curGamma = gamma;
    }

    private void writeOnImageView(ImageView view, Mode mode, Channel channel) {
        this.mode = mode;
        this.channel = channel;
        WritablePixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();
        WritableImage image = new WritableImage(picture.getWidth(), picture.getHeight());
        image.getPixelWriter().setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                format, picture.getIntArgb(this.curGamma, mode, channel),
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

        draw(this.picture, MODE_TO_FUNC.get(format).getLeft(), MODE_TO_FUNC.get(format).getRight());
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
        this.curGamma = 1.0;
    }

    public void onGamma(ActionEvent actionEvent) {
        showGammaInputDialog(new Stage());
        System.out.println(curGamma);
    }

    private void showGammaInputDialog(Stage primaryStage) {
        TextInputDialog dialog = new TextInputDialog("1.0");
        dialog.setTitle("Ввод гаммы");
        dialog.setHeaderText("Введите значение гаммы (от 0.0 до 128.0):");
        dialog.setContentText("Гамма:");

        dialog.showAndWait().ifPresent(gamma -> {
            try {
                double gammaValue = Double.parseDouble(gamma);
                if (gammaValue >= 0.0 && gammaValue <= 128.0) {
                    System.out.println("Гамма " + gammaValue);
                    this.curGamma = gammaValue;
                    draw(this.picture);
                } else {
                    System.out.println("Неверное значение гаммы. Значение должно быть в диапазоне от 0.0 до 128.0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка при парсинге значения гаммы.");
            }
        });
    }

}