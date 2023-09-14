package ru.squad1332.cg.entities;


public class PicturePNM implements Picture {
    private String formatType;
    private int width;
    private int height;
    private int maxColorValue;
    private Pixel[][] pixelData;

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
}
