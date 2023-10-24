package ru.squad1332.cg.entities;

import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;

import java.io.File;

public interface Picture {
    Pixel[] getPixelData();

    void setPixelData(Pixel[] pixelData);

    String getFormatType();

    int getWidth();

    int getHeight();

    String getPath();

    void setPath(String path);

    void writeToFile(File file, Mode mode, Channel channel);

    void setMode(Mode mode);

    void setChannel(Channel channel);

    int[] getIntArgb(Mode curMode, Channel curChannel, Mode mode, Channel channel, double curGamma, double interpretGamma);

    Mode getMode();

    Pixel[] applyGamma(Pixel[] pixelData, double curGamma, double newGamma, Mode curMode, Channel curChannel);

    Pixel[] convertGamma(Pixel[] pixelData, double curGamma, double newGamma, Mode curMode, Channel curChannel);
}
