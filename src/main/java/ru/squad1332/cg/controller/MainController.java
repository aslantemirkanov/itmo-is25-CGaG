package ru.squad1332.cg.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import ru.squad1332.cg.draw.GradientGenerator;
import ru.squad1332.cg.draw.Wu;
import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.gamma.GammaCorrection;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.services.PictureService;

import java.io.File;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
                picture.setPixelData(PicturePNM.OTHER_TO_RGB.get(mode).apply(picture.getPixelData(), channel));
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

    public void onDrawLine(ActionEvent actionEvent) {
        hideAll();
        lineForm.setVisible(true);
        canvas.setCursor(Cursor.CROSSHAIR);
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
        ditherForm.setVisible(true);

    }

    private void hideAll() {
        ditherForm.setVisible(false);
        lineForm.setVisible(false);
        imagesViews.setVisible(false);
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
            int inputHeight = Integer.parseInt(heightString.get()) ;
            if (inputWidth > 0 && inputWidth <= 1920 && inputHeight > 0 && inputHeight <= 1080) {
                /*this.picture.setPixelData(GammaCorrection.removeGamma(this.picture.getPixelData(), curGamma));
                */
                curGamma = 1;
                interpretGamma = 1;
                Picture gradientPicture = GradientGenerator.generateGradient(inputWidth, inputHeight);
                this.picture = gradientPicture;
                draw(gradientPicture);
            } else {
                System.out.println("Неверное значение ширины или высоты. Значение ширины должно быть в диапазоне от 1 до 1920, а высоты от 1 до 1080");
            }
        } catch (NumberFormatException e) {
            System.out.println("Значение ширины или высоты неверное. Вы ввели не целое число!!!");
        }

    }


}