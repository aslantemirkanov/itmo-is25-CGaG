package ru.squad1332.cg.filters;

import ru.squad1332.cg.entities.Pixel;

import java.util.Arrays;
import java.util.Comparator;

public class MedianFilter {
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

                Pixel medianPixel = calculateMedian(neighborhood);
                resultData[pixelIndex] = medianPixel;
            }
        }

        return resultData;
    }

    private static Pixel calculateMedian(Pixel[] neighborhood) {
        int size = neighborhood.length;

        double[][] colors = new double[size][3];

        for (int i = 0; i < size; i++) {
            colors[i] = neighborhood[i].getColors();
        }

        Arrays.sort(colors, Comparator.comparingDouble(a -> a[0]));
        double medianR = colors[size / 2][0];

        Arrays.sort(colors, Comparator.comparingDouble(a -> a[1]));
        double medianG = colors[size / 2][1];

        Arrays.sort(colors, Comparator.comparingDouble(a -> a[2]));
        double medianB = colors[size / 2][2];

        return new Pixel(medianR, medianG, medianB);
    }
}
