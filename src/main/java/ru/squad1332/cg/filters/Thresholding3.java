package ru.squad1332.cg.filters;

import ru.squad1332.cg.entities.Pixel;

public class Thresholding3 {

    public static Pixel[] filter(Pixel[] pixelData, int lowThreshold, int highThreshold) {

        Pixel[] resultData = new Pixel[pixelData.length];

        for (int i = 0; i < pixelData.length; i++) {
            double[] colors = pixelData[i].getColors();
            double gray = 0.3 * colors[0] + 0.59 * colors[1] + 0.11 * colors[2];
            double res;
            if (gray < (lowThreshold / 255.0)) {
                res = 0;
            } else if (gray < (double) highThreshold / 255.0) {
                res = 0.5;
                //res = (highThreshold- lowThreshold) / 2.0 / 255.0;
            } else {
                res = 1;
            }

            resultData[i] = new Pixel(res, res, res);
        }

        return resultData;
    }
}
