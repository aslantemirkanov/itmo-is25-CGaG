package ru.squad1332.cg.filters;

import ru.squad1332.cg.entities.Pixel;

public class GaussFilter {
    public static Pixel[] filter(Pixel[] pixelData, int width, int height, double sigma) {
        int radius = (int) Math.round(3 * sigma);

        Pixel[] resultData = new Pixel[pixelData.length];

        double[][] kernel = generateGaussianKernel(sigma, radius);

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

                Pixel blurredPixel = applyGaussianBlur(neighborhood, kernel);
                resultData[pixelIndex] = blurredPixel;
            }
        }

        return resultData;
    }

    private static double[][] generateGaussianKernel(double sigma, int radius) {
        int size = 2 * radius + 1;
        double[][] kernel = new double[size][size];

        double sum = 0.0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = i - radius;
                int y = j - radius;

                kernel[i][j] = Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                sum += kernel[i][j];
            }
        }


        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] /= sum;
            }
        }

        return kernel;
    }

    private static Pixel applyGaussianBlur(Pixel[] neighborhood, double[][] kernel) {
        int size = neighborhood.length;
        double[] sumColors = new double[3];

        for (int i = 0; i < size; i++) {
            double[] colors = neighborhood[i].getColors();
            for (int j = 0; j < 3; j++) {
                sumColors[j] += colors[j] * kernel[i / kernel.length][i % kernel.length];
            }
        }

        return new Pixel(sumColors[0], sumColors[1], sumColors[2]);
    }
}
