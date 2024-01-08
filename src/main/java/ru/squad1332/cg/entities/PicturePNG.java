package ru.squad1332.cg.entities;

import ru.squad1332.cg.convertor.ColorConvertor;
import ru.squad1332.cg.dither.DitheringService;
import ru.squad1332.cg.gamma.GammaCorrection;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.parsers.Chunk;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.zip.Deflater;

public class PicturePNG implements Picture {
    public int width;
    public int height;
    public double gamma;
    public boolean isSRGB;
    public byte bitDepth;
    public byte colorType;
    public byte compressionMethod;
    public byte filterMethod;
    public byte interlaceMethod;
    public Pixel[] pixelData;
    public int[] filterTypes;
    public int bytesPerPixel;

    private ArrayList<Chunk> chunks;
    private int[][] pallet;
    private String formatType;
    private int maxColorValue;
    private String path;
    private static final int SIGNATURE_SIZE = 8;
    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 137, 80, 78, 71, 13, 10, 26, 10};
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

    public void setChunks(ArrayList<Chunk> chunks) {
        this.chunks = chunks;
    }

    public void setPallet(int[][] pallet) {
        this.pallet = pallet;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setGamma(double newGamma) {
        gamma = newGamma;
    }

    @Override
    public double getGamma() {
        return gamma;
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
        return colorType;
    }

    @Override
    public void writeToFile(File file, Mode mode, Channel channel, double curGamma, Object dither, int bit) {
        Saver.writeToFile(file, this, mode, channel, curGamma, dither, bit);
    }

    public static Chunk compressIDAT(Pixel[] pixelData, int width, int height, int bytesPerPixel, int colorType){
        byte[] pixelBytes = new byte[width * height * bytesPerPixel + height];
        int rowStart = 1;
        int counter = 0;
        for (int row = 0; row < height; row++){
            pixelBytes[row * (width * bytesPerPixel + 1)] = 0;
            for (int col = 0; col < width; col++){
                int index = rowStart + col * bytesPerPixel;
                if (colorType == 0 || colorType == 4){
                    pixelBytes[index] = (byte) (((int)(pixelData[counter].getFirst() * 255)) & 0xFF);
                } else {
                    pixelBytes[index] = (byte) (((int)(pixelData[counter].getFirst() * 255)) & 0xFF);
                    pixelBytes[index + 1] = (byte) (((int)(pixelData[counter].getSecond() * 255)) & 0xFF);
                    pixelBytes[index + 2] = (byte) (((int)(pixelData[counter].getThird() * 255)) & 0xFF);
                }
                counter++;
            }
            rowStart += (bytesPerPixel * width) + 1;
        }

        byte[] data = deflateIDAT(pixelBytes);
        Chunk idatResult = new Chunk("IDAT", data);
        return idatResult;
    }


    public static byte[] deflateIDAT(byte[] input) {
        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(input.length);

        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
