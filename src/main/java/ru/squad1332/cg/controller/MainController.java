package ru.squad1332.cg.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.Pair;
import ru.squad1332.cg.dither.GradientGenerator;
import ru.squad1332.cg.draw.Wu;
import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.filters.*;
import ru.squad1332.cg.histogram.HistogramService;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.services.PictureService;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static ru.squad1332.cg.entities.PicturePNM.OTHER_TO_RGB;
import static ru.squad1332.cg.entities.PicturePNM.RGB_TO_OTHER;

public class MainController {
    private static final double scale = 1.05;
    private static final Map<String, Pair<Mode, Channel>> ACTION_TO_PAIR = getMapModeChannel();
    @FXML
    public HBox imagesViews;
    @FXML
    public VBox lineForm;
    @FXML
    public Slider lineWidthSlider;
    @FXML
    public Slider transparencySlider;
    @FXML
    public ColorPicker colorPicker;
    @FXML
    public Label lineWidthLabel;
    @FXML
    public Label transparencyLabel;
    @FXML
    public VBox ditherForm;
    @FXML
    public Slider bitSlider;
    @FXML
    public Label bitSliderLabel;
    @FXML
    public ComboBox ditherModeComboBox;
    private List<double[]> pickedPixels = new ArrayList<>();
    @FXML
    private ImageView firstChannel;
    @FXML
    private ImageView secondChannel;
    @FXML
    private ImageView thirdChannel;
    @FXML
    private Canvas canvas;
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
            this.file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
            if (this.file != null) {
                picture = pictureService.openPicture(this.file.getPath());
                if (picture.getFormatType().equals("PNG")){
                    curGamma = picture.getGamma();
                    interpretGamma = picture.getGamma();
                }
                //picture.setPixelData(PicturePNM.OTHER_TO_RGB.get(mode).apply(picture.getPixelData(), channel));
                draw(picture);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            this.errorMessage.setText("Не удалось открыть изображение");
        }
    }

    private void draw(Picture picture) {
        writeOnImageView(canvas, this.mode, this.channel);
    }

    private void draw(Picture picture, String choice, int bit) {
        writeOnImageView(canvas, mode, this.channel, choice, bit);
    }

    private void writeOnImageView(Canvas canvas, Mode mode, Channel channel, String choice, int bit) {
        canvas.setWidth(picture.getWidth());
        canvas.setHeight(picture.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        WritablePixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();
        gc.getPixelWriter().setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                format, picture.getIntArgb(this.mode, this.channel, mode, channel, curGamma, interpretGamma, choice, bit),
                0, picture.getWidth());
    }

    private void draw(Picture picture, Mode mode, Channel channel) {
        if (channel.equals(Channel.ALL)) {
            draw(picture, mode);
        } else {
            imagesViews.setVisible(false);
            firstChannel.setImage(null);
            secondChannel.setImage(null);
            thirdChannel.setImage(null);
            writeOnImageView(canvas, mode, channel);
        }
        this.mode = mode;
        this.channel = channel;

    }

    private void draw(Picture picture, Mode mode) {
        imagesViews.setVisible(true);
        writeOnImageView(firstChannel, mode, Channel.FIRST);
        writeOnImageView(secondChannel, mode, Channel.SECOND);
        writeOnImageView(thirdChannel, mode, Channel.THIRD);
        writeOnImageView(canvas, mode, Channel.ALL);
        this.mode = mode;
    }


    private void writeOnImageView(ImageView view, Mode mode, Channel channel) {
        WritablePixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();
        WritableImage image = new WritableImage(picture.getWidth(), picture.getHeight());

        image.getPixelWriter().setPixels(0, 0,
                picture.getWidth(), picture.getHeight(),
                format, picture.getIntArgb(this.mode, this.channel, mode, channel, curGamma, interpretGamma, null, 0),
                0, picture.getWidth());
        view.setImage(image);
    }
    //Todo посмотреть

    private void writeOnImageView(Canvas canvas, Mode mode, Channel channel) {
        writeOnImageView(canvas, mode, channel, null, 0);
    }


    @FXML
    protected void onSaveAs(ActionEvent event) {
        try {
            System.out.println("Save " + this.mode + " " + this.channel);
            this.errorMessage.setText("");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            File selectedFile = fileChooser.showSaveDialog(canvas.getScene().getWindow());
            if (selectedFile != null) {
                picture.writeToFile(selectedFile, this.mode, this.channel, curGamma, this.ditherModeComboBox.getValue(), (int) this.bitSlider.getValue());
            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            this.errorMessage.setText("Не удалось сохранить изображение");
        }
    }

    @FXML
    protected void colorConvertor(ActionEvent event) {
        clearBottomBar();
        MenuItem menuItem = (MenuItem) event.getSource();
        String format = menuItem.getId();

        draw(this.picture, ACTION_TO_PAIR.get(format).getLeft(), ACTION_TO_PAIR.get(format).getRight());
    }

    public void handleScroll(ScrollEvent event) {
        if (event.isControlDown()) {
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                zoomFactor /= scale;
            } else if (deltaY > 0) {
                zoomFactor *= scale;
            }
            canvas.setScaleX(zoomFactor);
            canvas.setScaleY(zoomFactor);

            event.consume();
        }
    }

    private void clean() {
        this.errorMessage.setText("");
        firstChannel.setImage(null);
        secondChannel.setImage(null);
        thirdChannel.setImage(null);
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
                } else {
                    System.out.println("Неверное значение гаммы. Значение должно быть в диапазоне от 0.0 до 128.0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка при парсинге значения гаммы.");
            }
        });
    }

    public void onConvertGamma(ActionEvent actionEvent) {
        showConvertGammaInputDialog(new Stage());
    }

    private void showConvertGammaInputDialog(Stage primaryStage) {
        TextInputDialog dialog = new TextInputDialog("0.0");
        dialog.setTitle("Ввод гаммы");
        dialog.setHeaderText("Введите значение гаммы (от 0.0 до 128.0):");
        dialog.setContentText("Гамма:");

        dialog.showAndWait().ifPresent(gamma -> {
            try {
                double newGamma = Double.parseDouble(gamma);
                if (newGamma >= 0.0 && newGamma <= 128.0) {
                    picture.setPixelData(picture.applyGamma(picture.getPixelData(), curGamma, newGamma, mode, channel));
                    picture.setGamma(newGamma);
                    curGamma = newGamma;
                    draw(picture, mode, channel);

                } else {
                    System.out.println("Неверное значение гаммы. Значение должно быть в диапазоне от 0.0 до 128.0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка при парсинге значения гаммы.");
            }
        });
    }


    public void drawLine(ActionEvent actionEvent) {
        canvas.setCursor(Cursor.DEFAULT);
        double[] first = pickedPixels.get(0);
        double[] second = pickedPixels.get(1);
        Color color = this.colorPicker.getValue();
        int width = (int) this.lineWidthSlider.getValue();
        double transparency = this.transparencySlider.getValue();
        Pixel pixel = new Pixel(color);
        Wu.drawLine(this.picture.getPixelData(),
                this.picture.getWidth(),
                this.picture.getHeight(),
                first[0], first[1],
                second[0], second[1],
                width, transparency, pixel);
        System.out.println("Draw");
        draw(this.picture);
        pickedPixels = new ArrayList<>();
    }

    public void pixelPicked(MouseEvent event) {
        if (lineForm.isVisible()) {
            System.out.println(event.getX() + " " + event.getY());
            pickedPixels.add(new double[]{event.getX(), event.getY()});
        }
    }

    public void initialize() {
        colorPicker.setValue(Color.BLACK);
        lineWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int lineWidthValue = (int) Math.round(newValue.doubleValue());
            lineWidthLabel.setText(String.valueOf(lineWidthValue));
        });

        transparencySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            transparencyLabel.setText(String.format("%.2f", newValue));
        });
        bitSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int bit = (int) Math.round(newValue.doubleValue());
            bitSliderLabel.setText(String.valueOf(bit));
        });

        ditherModeComboBox.getItems().addAll("Ordered",
                "Random",
                "Floyd-Steinberg",
                "Atkinson");
        lineWidthSlider.setValue(2);
        lineWidthSlider.setValue(1);
        transparencySlider.setValue(1);
    }

    public void applyDithering(ActionEvent actionEvent) {
        String choice = (String) ditherModeComboBox.getValue();
        System.out.println(choice);
        int bit = (int) this.bitSlider.getValue();
        interpretGamma = curGamma;
        draw(picture, choice, bit);
    }

    public void onDithering(ActionEvent actionEvent) {
        hideAll();
        imagesViews.setManaged(false);
        ditherForm.setVisible(true);
        ditherForm.setManaged(true);
    }

    public void onDrawLine(ActionEvent actionEvent) {
        hideAll();
        imagesViews.setManaged(false);
        lineForm.setVisible(true);
        lineForm.setManaged(true);
        canvas.setCursor(Cursor.CROSSHAIR);
    }

    private void hideAll() {
        clearBottomBar();
        imagesViews.setVisible(false);
    }

    public void clearBottomBar() {
        ditherForm.setVisible(false);
        ditherForm.setManaged(false);
        lineForm.setVisible(false);
        lineForm.setManaged(false);
    }

    public void onDrawGradient(ActionEvent actionEvent) {
/*        Picture picture1 = GradientGenerator.generateGradient(1920, 1080);
        this.picture = picture1;
        draw(picture1);*/
        showInputWidthHeight(new Stage());
    }

    public void showInputWidthHeight(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Введите размеры изображения с градиентом");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField widthTextField = new TextField("1920");
        TextField heightTextField = new TextField("1080");

        GridPane grid = new GridPane();
        grid.add(new Label("Ширина:"), 0, 0);
        grid.add(widthTextField, 1, 0);
        grid.add(new Label("Высота:"), 0, 1);
        grid.add(heightTextField, 1, 1);

        dialogPane.setContent(grid);
        AtomicReference<String> widthString = new AtomicReference<>();
        AtomicReference<String> heightString = new AtomicReference<>();

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                widthString.set(widthTextField.getText());
                heightString.set(heightTextField.getText());
            }
            return null;
        });

        dialog.showAndWait();
        System.out.println("Width: " + widthString.get());
        System.out.println("Height: " + heightString.get());

        try {
            int inputWidth = Integer.parseInt(widthString.get());
            int inputHeight = Integer.parseInt(heightString.get());
            if (inputWidth > 0 && inputWidth <= 1920 && inputHeight > 0 && inputHeight <= 1080) {
                /*this.picture.setPixelData(GammaCorrection.removeGamma(this.picture.getPixelData(), curGamma));
                */
                curGamma = 0;
                interpretGamma = 0;
                Picture gradientPicture = GradientGenerator.generateGradient(inputWidth, inputHeight);
                this.picture = gradientPicture;
                draw(gradientPicture);
            } else {
                System.out.println("Неверное значение доли автокоррекции. Значение должно быть в диапазоне [0, 0.5)");
            }
        } catch (NumberFormatException e) {
            System.out.println("Значение ширины или высоты неверное. Вы ввели не целое число!!!");
        }

    }

    public void onAutoCorrection(ActionEvent actionEvent) {
        showAutoCorrectionWindow(new Stage());
    }

    public void showAutoCorrectionWindow(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Введите долю игнорируемых пикселей в диапазоне [0, 0.5)");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField valueTextField = new TextField("0");

        GridPane grid = new GridPane();
        grid.add(new Label("Диапазон:"), 0, 0);
        grid.add(valueTextField, 1, 0);

        dialogPane.setContent(grid);
        AtomicReference<String> valueString = new AtomicReference<>();

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                valueString.set(valueTextField.getText());
            }
            return null;
        });

        dialog.showAndWait();
        System.out.println("AutoCorrection: " + valueString.get());

        try {
            double value = Double.parseDouble(valueString.get());
            if (value >= 0 && value < 0.5) {
                System.out.println(value + "ALL RIGHT");


                Pixel[] copy = new Pixel[picture.getPixelData().length];

                for (int i = 0; i < copy.length; i++) {
                    copy[i] = new Pixel(picture.getPixelData()[i].getFirst(),
                            picture.getPixelData()[i].getSecond(),
                            picture.getPixelData()[i].getThird());
                }
                boolean flag = false;
                copy = RGB_TO_OTHER.get(mode).apply(copy, channel);

                if (mode.equals(Mode.RGB) || mode.equals(Mode.CMY)) {
                    HistogramService.applyCorrection(copy, mode, channel, value);
                    flag = true;
                }

                if (mode.equals(Mode.HSL) && (channel.equals(Channel.ALL) || channel.equals(Channel.THIRD))) {
                    HistogramService.applyCorrection(copy, mode, Channel.THIRD, value);
                    flag = true;
                }

                if (mode.equals(Mode.HSV) && (channel.equals(Channel.ALL) || channel.equals(Channel.THIRD))) {
                    HistogramService.applyCorrection(copy, mode, Channel.THIRD, value);
                    flag = true;
                }

                if (mode.equals(Mode.YCBCR601) && (channel.equals(Channel.ALL) || channel.equals(Channel.FIRST))) {
                    HistogramService.applyCorrection(copy, mode, Channel.FIRST, value);
                    flag = true;
                }

                if (mode.equals(Mode.YCBCR709) && (channel.equals(Channel.ALL) || channel.equals(Channel.FIRST))) {
                    HistogramService.applyCorrection(copy, mode, Channel.FIRST, value);
                    flag = true;
                }

                if (mode.equals(Mode.YCOCG) && (channel.equals(Channel.ALL) || channel.equals(Channel.FIRST))) {
                    HistogramService.applyCorrection(copy, mode, Channel.FIRST, value);
                    flag = true;
                }

                copy = OTHER_TO_RGB.get(mode).apply(copy, channel);

                if (flag) {
                    this.picture.setPixelData(copy);
                    draw(picture, mode, channel);
                }
            } else {
                System.out.println("Неверное значение доли автокоррекции. Значение должно быть в диапазоне [0, 0.5)");
            }
        } catch (NumberFormatException e) {
            System.out.println("Значение доли автокоррекции неверное. Вы ввели не число!!!");
        }

    }

    public void cleanImageViews() {
        firstChannel.setImage(null);
        secondChannel.setImage(null);
        thirdChannel.setImage(null);
    }

    public void onDrawHistogram(ActionEvent actionEvent) {
        Pixel[] copy = new Pixel[picture.getPixelData().length];

        for (int i = 0; i < copy.length; i++) {
            copy[i] = new Pixel(picture.getPixelData()[i].getFirst(),
                    picture.getPixelData()[i].getSecond(),
                    picture.getPixelData()[i].getThird());
        }

        copy = RGB_TO_OTHER.get(mode).apply(copy, channel);

        HistogramService.drawHistogram(copy, mode, channel);

    }

    public void onThresholding2(ActionEvent actionEvent) {
        Thresholding2(new Stage());
    }

    public void Thresholding2(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Введите порог в диапазоне (0, 255)");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField valueTextField = new TextField("125");

        GridPane grid = new GridPane();
        grid.add(new Label("Порог:"), 0, 0);
        grid.add(valueTextField, 1, 0);

        dialogPane.setContent(grid);
        AtomicReference<String> valueString = new AtomicReference<>();

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                valueString.set(valueTextField.getText());
            }
            return null;
        });

        dialog.showAndWait();
        System.out.println("Threshold: " + valueString.get());

        try {
            int value = Integer.parseInt(valueString.get());
            if (value > 0 && value < 255) {
                System.out.println(value + "ALL RIGHT");
                picture.setPixelData(Thresholding2.filter(picture.getPixelData(), value));
                draw(picture);

            } else {
                System.out.println("Неверное значение доли автокоррекции. Значение должно быть в диапазоне (0, 255)");
            }
        } catch (NumberFormatException e) {
            System.out.println("Значение доли автокоррекции неверное. Вы ввели не число!!!");
        }

    }

    public void onThresholding3(ActionEvent actionEvent) {
        Thresholding3(new Stage());
    }

    public void Thresholding3(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Введите пороги в диапазоне (0, 255)");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField lowThresholdTextField = new TextField("100");
        TextField highThresholdTextField = new TextField("200");

        GridPane grid = new GridPane();
        grid.add(new Label("Нижний порог:"), 0, 0);
        grid.add(lowThresholdTextField, 1, 0);
        grid.add(new Label("Верхний порог:"), 0, 1);
        grid.add(highThresholdTextField, 1, 1);

        dialogPane.setContent(grid);
        AtomicReference<String> lowThresholdString = new AtomicReference<>();
        AtomicReference<String> highThresholdString = new AtomicReference<>();

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                lowThresholdString.set(lowThresholdTextField.getText());
                highThresholdString.set(highThresholdTextField.getText());
            }
            return null;
        });

        dialog.showAndWait();
        System.out.println("Low Threshold: " + lowThresholdString.get());
        System.out.println("High Threshold: " + highThresholdString.get());

        try {
            int lowThreshold = Integer.parseInt(lowThresholdString.get());
            int highThreshold = Integer.parseInt(highThresholdString.get());

            if (lowThreshold > 0 && lowThreshold < 255 && highThreshold > 0 && highThreshold < 255 && lowThreshold < highThreshold) {
                System.out.println(lowThreshold + " and " + highThreshold + " ALL RIGHT");
                picture.setPixelData(Thresholding3.filter(picture.getPixelData(), lowThreshold, highThreshold));
                draw(picture);
            } else {
                System.out.println("Неверные значения порогов. Значения должны быть в диапазоне (0, 255), и нижний порог должен быть меньше верхнего.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Значения порогов неверные. Вы ввели не число!!!");
        }
    }

    public void onOtsu2(ActionEvent actionEvent) {
        picture.setPixelData(Otsu2.filter(picture.getPixelData()));
        draw(picture);
    }

    public void onOtsu3(ActionEvent actionEvent) {
        picture.setPixelData(Otsu3.filter(picture.getPixelData()));
        draw(picture);
    }

    public void onMedianFilter(ActionEvent actionEvent) {
        Median(new Stage());
    }

    public void Median(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Введите радиус ядра");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField valueTextField = new TextField("1");

        GridPane grid = new GridPane();
        grid.add(new Label("Ядро:"), 0, 0);
        grid.add(valueTextField, 1, 0);

        dialogPane.setContent(grid);
        AtomicReference<String> valueString = new AtomicReference<>();

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                valueString.set(valueTextField.getText());
            }
            return null;
        });

        dialog.showAndWait();
        System.out.println("Kernel: " + valueString.get());

        try {
            int value = Integer.parseInt(valueString.get());
            if (value > 0) {
                System.out.println(value + "ALL RIGHT");
                picture.setPixelData(MedianFilter.filter(picture.getPixelData(), picture.getWidth(),
                        picture.getHeight(), value));
                draw(picture);

            } else {
                System.out.println("Неверное значение ядра");
            }
        } catch (NumberFormatException e) {
            System.out.println("Значение доли автокоррекции неверное. Вы ввели не число!!!");
        }

    }


    public void onLinearFilter(ActionEvent actionEvent) {
        Linear(new Stage());
    }

    public void Linear(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Введите радиус ядра");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField valueTextField = new TextField("1");

        GridPane grid = new GridPane();
        grid.add(new Label("Ядро:"), 0, 0);
        grid.add(valueTextField, 1, 0);

        dialogPane.setContent(grid);
        AtomicReference<String> valueString = new AtomicReference<>();

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                valueString.set(valueTextField.getText());
            }
            return null;
        });

        dialog.showAndWait();
        System.out.println("Kernel: " + valueString.get());

        try {
            int value = Integer.parseInt(valueString.get());
            if (value > 0) {
                System.out.println(value + "ALL RIGHT");
                picture.setPixelData(LinearFilter.filter(picture.getPixelData(), picture.getWidth(),
                        picture.getHeight(), value));
                draw(picture);

            } else {
                System.out.println("Неверное значение ядра");
            }
        } catch (NumberFormatException e) {
            System.out.println("Значение доли автокоррекции неверное. Вы ввели не число!!!");
        }

    }

    public void onUnsharpMasking(ActionEvent actionEvent) {
        UnsharpMask(new Stage());
    }

    public void UnsharpMask(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Введите пороги в диапазоне (0, 255)");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField amountField = new TextField("1");
        TextField sigmaField = new TextField("2");
        TextField thresholdField = new TextField("125");

        GridPane grid = new GridPane();
        grid.add(new Label("Amount:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Sigma:"), 0, 1);
        grid.add(sigmaField, 1, 1);
        grid.add(new Label("Threshold:"), 0, 2);
        grid.add(thresholdField, 1, 2);

        dialogPane.setContent(grid);
        AtomicReference<String> amountString = new AtomicReference<>();
        AtomicReference<String> sigmaString = new AtomicReference<>();
        AtomicReference<String> thresholdString = new AtomicReference<>();


        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                amountString.set(amountField.getText());
                sigmaString.set(sigmaField.getText());
                thresholdString.set(thresholdField.getText());
            }
            return null;
        });

        dialog.showAndWait();
        System.out.println("Amount: " + amountString.get());
        System.out.println("Sigma: " + sigmaString.get());
        System.out.println("Threshold: " + thresholdString.get());

        try {
            double amount = Double.parseDouble(amountString.get());
            double sigma = Double.parseDouble(sigmaString.get());
            int threshold = Integer.parseInt(thresholdString.get());

            if (amount > 0 && amount < 5
            && sigma > 0.1 && sigma < 12
            && threshold > 0 && threshold < 255) {
                System.out.println(" ALL RIGHT");
                picture.setPixelData(UnsharpMasking.filter(picture.getPixelData(), picture.getWidth(), picture.getHeight(),
                        amount, sigma, threshold));
                draw(picture);
            } else {
                System.out.println("Неверные значения порогов. Значения должны быть в диапазоне (0, 255), и нижний порог должен быть меньше верхнего.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Значения порогов неверные. Вы ввели не число!!!");
        }
    }

    public void onGaussFilter(ActionEvent actionEvent) {
        Gauss(new Stage());
    }

    public void Gauss(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Введите сигму");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField valueTextField = new TextField("1");

        GridPane grid = new GridPane();
        grid.add(new Label("Сигма:"), 0, 0);
        grid.add(valueTextField, 1, 0);

        dialogPane.setContent(grid);
        AtomicReference<String> valueString = new AtomicReference<>();

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                valueString.set(valueTextField.getText());
            }
            return null;
        });

        dialog.showAndWait();
        System.out.println("Kernel: " + valueString.get());

        try {
            double value = Double.parseDouble(valueString.get());
            if (value > 0) {
                System.out.println(value + "ALL RIGHT");
                picture.setPixelData(GaussFilter.filter(picture.getPixelData(), picture.getWidth(),
                        picture.getHeight(), value));
                draw(picture);

            } else {
                System.out.println("Неверное значение ядра");
            }
        } catch (NumberFormatException e) {
            System.out.println("Значение доли автокоррекции неверное. Вы ввели не число!!!");
        }

    }

    public void onSobelFilter(ActionEvent actionEvent) {
        picture.setPixelData(SobelFilter.filter(picture.getPixelData(), picture.getWidth(),
                picture.getHeight()));
        draw(picture);
    }


}