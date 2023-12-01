package ru.squad1332.cg.entities;

import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;

import java.io.File;

public interface Picture {
    Pixel[] getPixelData();

    void setPixelData(Pixel[] pixelData);

    String getFormatType();
    double getGamma();
    void setGamma(double gamma);

    int getWidth();

    int getHeight();

    String getPath();

    void setPath(String path);

    void writeToFile(File file, Mode mode, Channel channel, double curGamma, Object dither, int bit);

    int[] getIntArgb(Mode curMode, Channel curChannel, Mode mode, Channel channel, double curGamma, double interpretGamma, String choice, int bit);


    Pixel[] applyGamma(Pixel[] pixelData, double curGamma, double newGamma, Mode curMode, Channel curChannel);

    //Pixel[] convertGamma(Pixel[] pixelData, double curGamma, double newGamma, Mode curMode, Channel curChannel);
}
