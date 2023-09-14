package ru.squad1332.cg.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PicturePNM implements Picture {
    private String formatType;
    private int width;
    private int height;
    private int maxColorValue;
    private Pixel[][] pixelData;

}
