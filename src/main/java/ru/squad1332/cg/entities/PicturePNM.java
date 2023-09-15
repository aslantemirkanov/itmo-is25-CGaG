package ru.squad1332.cg.entities;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
    public Pixel[][] getPixelData() {
        return pixelData;
    }

    public void setPixelData(Pixel[][] pixelData) {
        this.pixelData = pixelData;
    }

    @Override
    public File getFile() {
        try {
            path = path + "\\new.pnm";
            File file = new File(path);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.wr
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(formatType + (char) (10));
            bufferedWriter.write(String.valueOf(width) + (char) (10) + String.valueOf(height) + (char) (10));
            bufferedWriter.write(String.valueOf(maxColorValue) + (char) (10));
            bufferedWriter.wr
            StringBuilder data = new StringBuilder();
            if (formatType.equals("P5")) {
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        data.append((char) pixelData[i][j].getRgb()[0]);
                    }
                }
            }
            if (formatType.equals("P6")) {
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        data.append((char) (pixelData[i][j].getRgb()[0] < 0 ? 256 + pixelData[i][j].getRgb()[0] :
                                pixelData[i][j].getRgb()[0]));
                        data.append((char) (pixelData[i][j].getRgb()[1] < 0 ? 256 + pixelData[i][j].getRgb()[1] :
                                pixelData[i][j].getRgb()[1]));
                        data.append((char) (pixelData[i][j].getRgb()[2] < 0 ? 256 + pixelData[i][j].getRgb()[2] :
                                pixelData[i][j].getRgb()[2]));
                    }
                }
            }
            bufferedWriter.write(String.valueOf(data));
            bufferedWriter.close();
            fileWriter.close();
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
