package ru.squad1332.cg.filters;

import ru.squad1332.cg.entities.Pixel;

public class SobelFilter {
    public static Pixel[] filter(Pixel[] pixelData, int width, int height) {
        Pixel[] resultData = new Pixel[pixelData.length];

        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixelIndex = i * width + j;

                double gradientXRed = applyKernel(pixelData, width, height, i, j, sobelX, Channel.RED);
                double gradientYRed = applyKernel(pixelData, width, height, i, j, sobelY, Channel.RED);

                double gradientXGreen = applyKernel(pixelData, width, height, i, j, sobelX, Channel.GREEN);
                double gradientYGreen = applyKernel(pixelData, width, height, i, j, sobelY, Channel.GREEN);

                double gradientXBlue = applyKernel(pixelData, width, height, i, j, sobelX, Channel.BLUE);
                double gradientYBlue = applyKernel(pixelData, width, height, i, j, sobelY, Channel.BLUE);

                double gradientMagnitudeRed = Math.sqrt(gradientXRed * gradientXRed + gradientYRed * gradientYRed);
                double gradientMagnitudeGreen = Math.sqrt(gradientXGreen * gradientXGreen + gradientYGreen * gradientYGreen);
                double gradientMagnitudeBlue = Math.sqrt(gradientXBlue * gradientXBlue + gradientYBlue * gradientYBlue);


                double res = (gradientMagnitudeRed + gradientMagnitudeGreen + gradientMagnitudeBlue) / 3;

                resultData[pixelIndex] = new Pixel(res,res,res);
            }
        }

        return resultData;
    }

    private static double applyKernel(Pixel[] pixelData, int width, int height, int row, int col, int[][] kernel, Channel channel) {
        int kernelSize = kernel.length;
        int radius = kernelSize / 2;
        double sum = 0.0;

        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                int neighborRow = Math.min(Math.max(row - radius + i, 0), height - 1);
                int neighborCol = Math.min(Math.max(col - radius + j, 0), width - 1);
                int neighborIndex = neighborRow * width + neighborCol;

                double pixelValue = switch (channel) {
                    case RED -> pixelData[neighborIndex].getColors()[0];
                    case GREEN -> pixelData[neighborIndex].getColors()[1];
                    case BLUE -> pixelData[neighborIndex].getColors()[2];
                };

                sum += pixelValue * kernel[i][j];
            }
        }

        return sum;
    }

    private enum Channel {
        RED, GREEN, BLUE
    }
}
