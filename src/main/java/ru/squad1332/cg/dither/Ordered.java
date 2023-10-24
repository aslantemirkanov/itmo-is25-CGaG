package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.Pixel;

public class Ordered implements DitheringAlgorithm {
    int m = 8;
    private double[][] thresholdMap;

    public Ordered() {
        thresholdMap = new double[][]{
                {0, 32, 8, 40, 2, 34, 10, 42},
                {48, 16, 56, 24, 50, 18, 58, 26},
                {12, 44, 4, 36, 14, 46, 6, 38},
                {60, 28, 52, 20, 62, 30, 54, 22},
                {3, 35, 11, 43, 1, 33, 9, 41},
                {51, 19, 59, 27, 49, 17, 57, 25},
                {15, 47, 7, 39, 13, 45, 5, 37},
                {63, 31, 55, 23, 61, 29, 53, 21}
        };
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                thresholdMap[i][j] /= 64;
            }
        }
    }

    @Override
    public void apply(Pixel[] pixels, int bit, int w, int h) {
        int pointer = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Pixel pixel = pixels[pointer++];
                double[][] values = new double[][]{{0, 0}, {0, 0}, {0, 0}};
                int step = (int) (255 / Math.pow(2, bit));
                for (int c = 0; c < 3; c++) {
                    for (int t = 0; t < 255; t += step) {
                        if ((double) t / 255 >= pixel.getColors()[c]) {
                            values[c][0] = Math.max(0, (t - step) / 255);
                            values[c][1] = (double) t / 255;
                            break;
                        }
                    }
                    if (pixel.getColors()[c] >= thresholdMap[i % 8][j % 8]) {
                        pixel.getColors()[c] = values[c][1];
                    } else {
                        pixel.getColors()[c] = values[c][0];
                    }
                }
            }
        }
    }

}
