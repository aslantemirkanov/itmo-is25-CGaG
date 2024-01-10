package ru.squad1332.cg.filters;

import ru.squad1332.cg.entities.Pixel;

public class Thresholding2 {

    public static Pixel[] filter(Pixel[] pixelData, int threshold) {
        Pixel[] resultData = new Pixel[pixelData.length];

        double hold = threshold / 255.0;

        for (int i = 0; i < pixelData.length; i++) {
            double[] colors = pixelData[i].getColors();
            double gray = 0.3 * colors[0] + 0.59 * colors[1] + 0.11 * colors[2];
            gray = gray > hold ? 1 : 0;
            resultData[i] = new Pixel(gray, gray, gray);
        }

        return resultData;
    }
}

