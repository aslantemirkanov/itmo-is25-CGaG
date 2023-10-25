package ru.squad1332.cg.entities;


import ru.squad1332.cg.convertor.ColorConvertor;
import ru.squad1332.cg.dither.DitheringService;
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

    public static final Map<Mode, BiFunction<Pixel[], Channel, Pixel[]>> RGB_TO_OTHER = Map.of(
            Mode.RGB, ColorConvertor::convertRgbToRgb,
            Mode.HSL, ColorConvertor::convertRgbToHsl,
            Mode.HSV, ColorConvertor::convertRgbToHsv,
            Mode.YCBCR601, ColorConvertor::convertRgbToYCbCr601,
            Mode.YCBCR709, ColorConvertor::convertRgbToYCbCr709,
            Mode.YCOCG, ColorConvertor::convertRgbToYCoCg,
            Mode.CMY, ColorConvertor::convertRgbToCmy
    );

    public static final Map<Mode, BiFunction<Pixel[], Channel, Pixel[]>> OTHER_TO_RGB = Map.of(
            Mode.RGB, ColorConvertor::convertRgbToRgb,
            Mode.HSL, ColorConvertor::convertHslToRgb,
            Mode.HSV, ColorConvertor::convertHsvToRgb,
            Mode.YCBCR601, ColorConvertor::convertYCbCr601ToRgb,
            Mode.YCBCR709, ColorConvertor::convertYCbCr709ToRgb,
            Mode.YCOCG, ColorConvertor::convertYCoCgToRgb,
            Mode.CMY, ColorConvertor::convertCmyToRgb
    );
    public static final Map<Mode, Function<Pixel, Pixel>> OTHER_TO_RGB_ONE = Map.of(
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

    @Override
    public void setPixelData(Pixel[] pixelData) {
        this.pixelData = pixelData;
    }

    @Override
    public void writeToFile(File file, Mode mode, Channel channel, double curGamma, Object dither, int bit) {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file))) {
/*
            Pixel[] pixelsData = new Pixel[this.pixelData.length];
            for (int i = 0; i < this.pixelData.length; i++) {
                pixelsData[i] = new Pixel(this.pixelData[i].getFirst(),
                        this.pixelData[i].getSecond(),
                        this.pixelData[i].getThird());
            }

            pixelsData = pixelConversion(Mode.RGB, Channel.ALL, mode, channel, curGamma, curGamma);
            //pixelsData = OTHER_TO_RGB.get(this.mode).apply(pixelsData, this.channel);
            //pixelsData = RGB_TO_OTHER.get(mode).apply(pixelsData, channel);

            if (channel.equals(Channel.ALL)) {
                dataOutputStream.writeBytes(formatType + (char) (10));
                dataOutputStream.writeBytes(String.valueOf(width) + (char) (32) + String.valueOf(height) + (char) (10));
                dataOutputStream.writeBytes(String.valueOf(maxColorValue) + (char) (10));

                if (formatType.equals("P6")) {
                    byte[] pixels = new byte[3 * height * width];
                    int cur = 0;
                    for (int i = 0; i < pixelsData.length; i++) {
                        cur = i * 3;
                        Pixel curPixel = pixelsData[i];
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
                }*/
            if (dither != null) {
                DitheringService.applyDithering(pixelData, (String) dither, bit, width, height);
            }
            pixelData = RGB_TO_OTHER.get(mode).apply(pixelData, channel);
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
                    };
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
                        case FIRST -> first = (int) (pixelData[i].getColors()[0] * 255);
                        case SECOND -> first = (int) (pixelData[i].getColors()[1] * 255);
                        case THIRD -> first = (int) (pixelData[i].getColors()[2] * 255);
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
    public int[] getIntArgb(Mode curMode, Channel curChannel, Mode mode, Channel channel, double curGamma, double interpretGamma, String choice, int bit) {
        System.out.println("Режим картинки " + curMode + " " + curChannel);
        System.out.println("Режим текущий " + mode + " " + channel);

        int[] intRgba = new int[pixelData.length];

        Pixel[] pixels = pixelConversion(curMode, curChannel, mode, channel, curGamma, interpretGamma, choice, bit);

        //pixels = applyGamma(pixels, curGamma, interpretGamma, mode, channel);

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


    public Pixel[] pixelConversion(Mode curMode, Channel curChannel, Mode mode, Channel channel, double curGamma,
                                   double newGamma, String choice, int bit) {
        Pixel[] copy = new Pixel[this.pixelData.length];
        for (int i = 0; i < this.pixelData.length; i++) {
            copy[i] = new Pixel(this.pixelData[i].getFirst(),
                    this.pixelData[i].getSecond(),
                    this.pixelData[i].getThird());
        }

        if (choice != null) {
            DitheringService.applyDithering(copy, choice, bit, width, height);
        }

        copy = applyGamma(copy, curGamma, newGamma, mode, channel);
        copy = RGB_TO_OTHER.get(mode).apply(copy, channel);
        System.out.println("OTHER");
        copy = OTHER_TO_RGB.get(mode).apply(copy, channel);
        System.out.println("RGB AGAIN");
        return copy;
    }

    public Pixel[] applyGamma(Pixel[] pixelData, double curGamma, double newGamma,
                              Mode curMode, Channel curChannel) {

        if (curGamma == newGamma) {
            return pixelData;
        }

        Pixel[] copy = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            copy[i] = new Pixel(pixelData[i].getFirst(),
                    pixelData[i].getSecond(),
                    pixelData[i].getThird());
        }

        //copy = OTHER_TO_RGB.get(curMode).apply(copy, curChannel);
        GammaCorrection.removeGamma(copy, curGamma);
        GammaCorrection.assignGamma(copy, newGamma);
        //copy = RGB_TO_OTHER.get(curMode).apply(copy, curChannel);

        System.out.println("INTERPRET GAMMA = " + newGamma + " APPLY");
        return copy;
    }

}
