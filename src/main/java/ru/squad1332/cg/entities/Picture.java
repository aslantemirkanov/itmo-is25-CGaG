package ru.squad1332.cg.entities;

import java.io.File;

public interface Picture {
    int[] getPixelData();
    String getFormatType();
    int getWidth();
    int getHeight();

    String getPath();
    void setPath(String path);
    File getFile();
}
