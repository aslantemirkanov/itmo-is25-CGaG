package ru.squad1332.cg.draw;

import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.entities.Pixel;

public class GradientGenerator {
    public static Picture generateGradient(int w, int h) {
        PicturePNM result = new PicturePNM();
        result.setFormatType("P6");
        result.setHeight(h);
        result.setWidth(w);
        result.setMaxColorValue(255);
        Pixel[] pixels = new Pixel[w * h];
        int pointer = 0;
        for (int j = 0; j < h; j++) {
            double start = 0;
            double step = (double) 255 / (w * 255);
            for (int i = 0; i < w; i++) {
                pixels[pointer++] = new Pixel(start, start, start);
                start += step;
            }
        }
        result.setPixelData(pixels);
        return result;
    }
}
