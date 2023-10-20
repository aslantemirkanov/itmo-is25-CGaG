package ru.squad1332.cg.entities;

import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;

import java.io.File;

public interface Picture {
    Pixel[] getPixelData();

    String getFormatType();

    int getWidth();

    int getHeight();

    String getPath();

    void setPath(String path);

    void writeToFile(File file, Mode mode, Channel channel);

    int[] getIntArgb();

    void setMode(Mode mode);
    void setChannel(Channel channel);


    int[] getIntArgb(Mode mode, Channel channel);
}
