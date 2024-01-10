package ru.squad1332.cg.filters;

import ru.squad1332.cg.entities.Pixel;

import java.util.Arrays;
import java.util.Comparator;

public class LinearFilter {
    public static Pixel[] filter(Pixel[] pixelData, int width, int height, int radius) {

        Pixel[] resultData = new Pixel[pixelData.length];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixelIndex = i * width + j;
                int startRow = Math.max(0, i - radius);
                int endRow = Math.min(height - 1, i + radius);
                int startCol = Math.max(0, j - radius);
                int endCol = Math.min(width - 1, j + radius);

                Pixel[] neighborhood = new Pixel[(endRow - startRow + 1) * (endCol - startCol + 1)];
                int index = 0;

                for (int row = startRow; row <= endRow; row++) {
                    for (int col = startCol; col <= endCol; col++) {
                        int neighborIndex = row * width + col;
                        neighborhood[index++] = pixelData[neighborIndex];
                    }
                }

                Pixel averagedPixel = calculateAverage(neighborhood);
                resultData[pixelIndex] = averagedPixel;
            }
        }

        return resultData;
    }

    private static Pixel calculateAverage(Pixel[] neighborhood) {
        int size = neighborhood.length;
        double sumR = 0.0;
        double sumG = 0.0;
        double sumB = 0.0;

        for (int i = 0; i < size; i++) {
            double[] colors = neighborhood[i].getColors();
            sumR += colors[0];
            sumG += colors[1];
            sumB += colors[2];
        }

        double averageR = sumR / size;
        double averageG = sumG / size;
        double averageB = sumB / size;

        return new Pixel(averageR, averageG, averageB);
    }
}
