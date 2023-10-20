package ru.squad1332.cg.entities;


import ru.squad1332.cg.convertor.ColorConvertor;
import ru.squad1332.cg.gamma.GammaCorrection;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PicturePNM implements Picture {

    private static final Map<Mode, BiFunction<Pixel[], Channel, Pixel[]>> RGB_TO_OTHER = Map.of(
            Mode.RGB, ColorConvertor::convertRgbToRgb,
            Mode.HSL, ColorConvertor::convertRgbToHsl,
            Mode.HSV, ColorConvertor::convertRgbToHsv,
            Mode.YCBCR601, ColorConvertor::convertRgbToYCbCr601,
            Mode.YCBCR709, ColorConvertor::convertRgbToYCbCr709,
            Mode.YCOCG, ColorConvertor::convertRgbToYCoCg,
            Mode.CMY, ColorConvertor::convertRgbToCmy
    );

    private static final Map<Mode, BiFunction<Pixel[], Channel, Pixel[]>> OTHER_TO_RGB = Map.of(
            Mode.RGB, ColorConvertor::convertRgbToRgb,
            Mode.HSL, ColorConvertor::convertHslToRgb,
            Mode.HSV, ColorConvertor::convertHsvToRgb,
            Mode.YCBCR601, ColorConvertor::convertYCbCr601ToRgb,
            Mode.YCBCR709, ColorConvertor::convertYCbCr709ToRgb,
            Mode.YCOCG, ColorConvertor::convertYCoCgToRgb,
            Mode.CMY, ColorConvertor::convertCmyToRgb
    );
    private static final Map<Mode, Function<Pixel, Pixel>> OTHER_TO_RGB_ONE = Map.of(
            Mode.RGB, ColorConvertor::convertRgbToRgbOne,
            Mode.HSL, ColorConvertor::convertHslToRgbOne,
            Mode.HSV, ColorConvertor::convertHsvToRgbOne,
            Mode.YCBCR601, ColorConvertor::convertYCbCr601ToRgbOne,
            Mode.YCBCR709, ColorConvertor::convertYCbCr709ToRgbOne,
            Mode.YCOCG, ColorConvertor::convertYCoCgToRgbOne,
            Mode.CMY, ColorConvertor::convertCmyToRgbOne
    );
    private String formatType;
    private int width;
    private int height;
    private int maxColorValue;
    private String path;
    private Pixel[] pixelData;
    private Mode mode = Mode.RGB;
    private Channel channel = Channel.ALL;


    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    public int getMaxColorValue() {
        return maxColorValue;
    }

    public void setMaxColorValue(int maxColorValue) {
        this.maxColorValue = maxColorValue;
    }

    @Override
    public Pixel[] getPixelData() {
        return pixelData;
    }

    public void setPixelData(Pixel[] pixelData) {
        this.pixelData = pixelData;
    }

    @Override
    public void writeToFile(File file, Mode mode, Channel channel) {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file))) {
            if (channel.equals(Channel.ALL)) {
                dataOutputStream.writeBytes(formatType + (char) (10));
                dataOutputStream.writeBytes(String.valueOf(width) + (char) (32) + String.valueOf(height) + (char) (10));
                dataOutputStream.writeBytes(String.valueOf(maxColorValue) + (char) (10));

                if (formatType.equals("P6")) {
                    byte[] pixels = new byte[3 * height * width];
                    int cur = 0;
                    for (int i = 0; i < pixelData.length; i++) {
                        cur = i * 3;
                        Pixel curPixel = pixelData[i];
                        int alpha = 255;
                        double[] convert = curPixel.getColors();
                        int first = (int) (convert[0] * 255);
                        int second = (int) (convert[1] * 255);
                        int third = (int) (convert[2] * 255);
                        pixels[cur] = (byte) (first > 127 ? first - 256 : first);
                        pixels[cur + 1] = (byte) (second > 127 ? second - 256 : second);
                        pixels[cur + 2] = (byte) (third > 127 ? third - 256 : third);
                    }
                    dataOutputStream.write(pixels);
                    dataOutputStream.close();
                }

                if (formatType.equals("P5")) {
                    byte[] pixels = new byte[height * width];
                    for (int i = 0; i < pixelData.length; i++) {
                        int first = (int) (pixelData[i].getColors()[0] * 255);
                        pixels[i] = (byte) (first > 127 ? first - 256 : first);
                    }
                    dataOutputStream.write(pixels);
                }
            } else {
                dataOutputStream.writeBytes("P5" + (char) (10));
                dataOutputStream.writeBytes(String.valueOf(width) + (char) (32) + String.valueOf(height) + (char) (10));
                dataOutputStream.writeBytes(String.valueOf(maxColorValue) + (char) (10));

                byte[] pixels = new byte[height * width];
                for (int i = 0; i < pixelData.length; i++) {
                    int first = 0;
                    switch (channel) {
                        case FIRST -> first =  (int) (pixelData[i].getColors()[0] * 255);
                        case SECOND -> first =  (int) (pixelData[i].getColors()[1] * 255);
                        case THIRD -> first =  (int) (pixelData[i].getColors()[2] * 255);
                    }
                    pixels[i] = (byte) (first > 127 ? first - 256 : first);
                }
                dataOutputStream.write(pixels);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int[] getIntArgb() {
        return getIntArgb(1.0);
    }

    @Override
    public int[] getIntArgb(double gamma) {
        return getIntArgb(gamma, this.mode, this.channel);
    }

    @Override
    public int[] getIntArgb(double gamma, Mode mode, Channel channel) {
        int[] intRgba = new int[pixelData.length];
        Pixel[] pixels = OTHER_TO_RGB.get(this.mode).apply(this.pixelData, this.channel);
        pixels = RGB_TO_OTHER.get(mode).apply(pixels, channel);
        pixels = OTHER_TO_RGB.get(mode).apply(pixels, channel);
        pixels = GammaCorrection.convertGamma(pixels, 1.0, gamma);
        for (int i = 0; i < pixels.length; i++) {
            double[] rgba = pixels[i].getColors();
            int r = (int) (rgba[0] * 255);
            int g = (int) (rgba[1] * 255);
            int bl = (int) (rgba[2] * 255);
            int alpha = 255;
            intRgba[i] = (alpha << 24) + (r << 16) + (g << 8) + (bl);
        }
        return intRgba;
    }


    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
