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

    @Override
    public double getGamma() {
        return 0;
    }

    @Override
    public void setGamma(double gamma) {
        return;
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
        Saver.writeToFile(file, this, mode, channel, curGamma, dither, bit);
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
        copy = applyGamma(copy, curGamma, newGamma, mode, channel);

        if (choice != null) {
            DitheringService.applyDithering(copy, choice, this.formatType, bit, width, height, newGamma);
        }

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

    @Override
    public byte getColorType() {
        if (formatType.equals("P6")){
            return 2;
        } else {
            return 0;
        }
    }

}
