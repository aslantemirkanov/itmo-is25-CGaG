package ru.squad1332.cg.filters;

import ru.squad1332.cg.entities.Pixel;

public class Otsu3 {

    public static int[][] countThreshold(Pixel[] pixelData) {
        int min = 0;
        int max = 255;

        int histSize = max - min + 1;
        int[] histR = new int[histSize];
        int[] histG = new int[histSize];
        int[] histB = new int[histSize];

        for (Pixel pixel : pixelData) {
            histR[(int) (pixel.getFirst() * 255)]++;
            histG[(int) (pixel.getSecond() * 255)]++;
            histB[(int) (pixel.getThird() * 255)]++;
        }

        int[][] thresholds = new int[3][2];

        for (int channel = 0; channel < 3; channel++) {
            int m = 0;
            int n = 0;
            int[] hist = (channel == 0) ? histR : (channel == 1) ? histG : histB;

            for (int t = 0; t <= max - min; t++) {
                m += t * hist[t];
                n += hist[t];
            }

            float maxSigma = -1;
            int[] threshold = new int[2];

            for (int t1 = 0; t1 < max - min; t1++) {
                int alpha1 = 0;
                int beta1 = 0;

                for (int t = 0; t <= t1; t++) {
                    alpha1 += t * hist[t];
                    beta1 += hist[t];
                }

                int alpha2 = 0;
                int beta2 = 0;

                for (int t2 = t1 + 1; t2 < max - min; t2++) {
                    alpha2 += t2 * hist[t2];
                    beta2 += hist[t2];

                    float w1 = (float) beta1 / n;
                    float w2 = (float) beta2 / n;

                    float mu1 = (beta1 == 0) ? 0 : (float) alpha1 / beta1;
                    float mu2 = (beta2 == 0) ? 0 : (float) alpha2 / beta2;

                    float sigma = w1 * w2 * (mu1 - mu2) * (mu1 - mu2);

                    if (sigma > maxSigma) {
                        maxSigma = sigma;
                        threshold[0] = t1;
                        threshold[1] = t2;
                    }
                }
            }

            thresholds[channel] = threshold;
        }

        return thresholds;
    }

    public static Pixel[] filter(Pixel[] pixelData) {
        Pixel[] resultData = new Pixel[pixelData.length];

        int totalPixels = pixelData.length;

        int[][] thresholds = countThreshold(pixelData);

        double[] doubleThresholds1 = new double[3];
        double[] doubleThresholds2 = new double[3];

        for (int i = 0; i < 3; i++) {
            doubleThresholds1[i] = thresholds[i][0] / 255.0;
            doubleThresholds2[i] = thresholds[i][1] / 255.0;
            System.out.println(thresholds[i][0] + " !!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(thresholds[i][1] + " !!!!!!!!!!!!!!!!!!!!!!!");
        }

        double[][] doubleThresholds = new double[3][2];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                doubleThresholds[i][j] = thresholds[i][j] / 255.0;
                System.out.println(thresholds[i][j] + "!!!!!!!!!!!!!");
            }
        }

        for (int i = 0; i < totalPixels; i++) {
            double[] colors = pixelData[i].getColors();
            double redBinary = getBinaryValue(colors[0], doubleThresholds[0][0], doubleThresholds[0][1]);
            double greenBinary = getBinaryValue(colors[1], doubleThresholds[1][0], doubleThresholds[1][1]);
            double blueBinary = getBinaryValue(colors[2], doubleThresholds[2][0], doubleThresholds[2][1]);
            resultData[i] = new Pixel(redBinary, greenBinary, blueBinary);
        }

        return resultData;

    }

    private static double getBinaryValue(double intensity, double lowThreshold, double highThreshold) {
        return (intensity < lowThreshold) ? 0 : (intensity > highThreshold) ? 1 : 0.5;
    }
}


