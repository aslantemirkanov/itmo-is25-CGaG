package ru.squad1332.cg.entities;


import ru.squad1332.cg.convertor.ColorConvertor;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.modes.Channel;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiFunction;

public class PicturePNM implements Picture {


    private static final Map<Mode, BiFunction<Pixel[], Channel, int[]>> MODE_TO_FUNC = Map.of(
            Mode.RGB, ColorConvertor::convertToRgb,
            Mode.HSL, ColorConvertor::convertToHSL,
            Mode.HSV, ColorConvertor::convertToHSV,
            Mode.YCBCR601, ColorConvertor::convertToYCbCr601,
            Mode.YCBCR709, ColorConvertor::convertToYCbCr709,
            Mode.YCOCG, ColorConvertor::convertToYCoCg,
            Mode.CMY, ColorConvertor::convertToCmy
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

    public void setPixelData(Pixel[] pixelData) {
        this.pixelData = pixelData;
    }

    @Override
    public void writeToFile(File file, Mode mode, Channel channel) {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file))) {

            dataOutputStream.writeBytes(formatType + (char) (10));
            dataOutputStream.writeBytes(String.valueOf(width) + (char) (32) + String.valueOf(height) + (char) (10));
            dataOutputStream.writeBytes(String.valueOf(maxColorValue) + (char) (10));

            if (formatType.equals("P6")) {
                byte[] pixels = new byte[3 * height * width];
                int cur = 0;
                for (int i = 0; i < pixelData.length; i++) {
                    cur = i * 3;
                    Pixel curPixel = pixelData[i];
                    int alpha = (int) (curPixel.getAlpha()* 255);
                    int[] convert = ColorConvertor.convertRgb(curPixel, mode, channel);
                    int red = convert[0];
                    int green = convert[1];
                    int blue = convert[2];
                    pixels[cur] = (byte) (red > 127 ? red - 256 : red);
                    pixels[cur + 1] = (byte) (green > 127 ? green - 256 : green);
                    pixels[cur + 2] = (byte) (blue > 127 ? blue - 256 : blue);
                }
                dataOutputStream.write(pixels);
                dataOutputStream.close();
            }

            if (formatType.equals("P5")) {
                byte[] pixels = new byte[height * width];
                for (int i = 0; i < pixelData.length; i++) {
                    int red = (int) (pixelData[i].getRgba()[0] * 255);
                    pixels[i] = (byte) (red > 127 ? red - 256 : red);
                }
                dataOutputStream.write(pixels);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int[] getIntArgb(Mode mode, Channel channel) {
        return MODE_TO_FUNC.get(mode).apply(this.pixelData, channel);
    }
}
