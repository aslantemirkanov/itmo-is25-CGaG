package ru.squad1332.cg.entities;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PicturePNM implements Picture {
    private String formatType;
    private int width;
    private int height;
    private int maxColorValue;

    private String path;
    private int[] pixelData;

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

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMaxColorValue() {
        return maxColorValue;
    }

    public void setMaxColorValue(int maxColorValue) {
        this.maxColorValue = maxColorValue;
    }

    @Override
    public int[] getPixelData() {
        return pixelData;
    }

    public void setPixelData(int[] pixelData) {
        this.pixelData = pixelData;
    }

    @Override
    public void writeToFile(File file) {
        try(DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file))) {

            dataOutputStream.writeBytes(formatType + (char) (10));
            dataOutputStream.writeBytes(String.valueOf(width) + (char) (32) + String.valueOf(height) + (char) (10));
            dataOutputStream.writeBytes(String.valueOf(maxColorValue) + (char) (10));

            if (formatType.equals("P6")) {
                byte[] pixels = new byte[3 * height * width];
                int cur = 0;
                for (int i = 0; i < pixelData.length; i++) {
                    cur = i * 3;
                    int curPixel = pixelData[i];
                    int alpha = ((curPixel >> 24) & 0xff);
                    int red = ((curPixel >> 16) & 0xff);
                    int green = ((curPixel >> 8) & 0xff);
                    int blue = ((curPixel) & 0xff);
                    pixels[cur] = (byte) (red > 127 ? red - 256 : red);
                    pixels[cur + 1] = (byte) (green > 127 ? green - 256 : green);
                    pixels[cur + 2] = (byte) (blue > 127 ? blue - 256 : blue);
                }
                dataOutputStream.write(pixels);
                dataOutputStream.close();
            }

            if (formatType.equals("P5")){
                byte[] pixels = new byte[height * width];
                for (int i = 0; i < pixelData.length; i++) {
                    int curPixel = pixelData[i];
                    int red = ((curPixel >> 16) & 0xff);
                    pixels[i] = (byte) (red > 127 ? red - 256 : red);
                }
                dataOutputStream.write(pixels);
                dataOutputStream.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
