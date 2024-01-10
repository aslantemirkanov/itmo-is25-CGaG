package ru.squad1332.cg.filters;

import ru.squad1332.cg.entities.Pixel;

public class Otsu2 {

    public static int[] countThreshold(Pixel[] pixelData) {
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

        int[] thresholds = new int[3];

        for (int channel = 0; channel < 3; channel++) {
            int m = 0;
            int n = 0;
            int[] hist = (channel == 0) ? histR : (channel == 1) ? histG : histB;

            for (int t = 0; t <= max - min; t++) {
                m += t * hist[t];
                n += hist[t];
            }

            float maxSigma = -1;
            int threshold = 0;

            int alpha1 = 0;
            int beta1 = 0;

            for (int t = 0; t < max - min; t++) {
                alpha1 += t * hist[t];
                beta1 += hist[t];

                float w1 = (float) beta1 / n;
                float a = (float) alpha1 / beta1 - (float) (m - alpha1) / (n - beta1);
                float sigma = w1 * (1 - w1) * a * a;

                if (sigma > maxSigma) {
                    maxSigma = sigma;
                    threshold = t;
                }
            }

            thresholds[channel] = threshold;
        }

        return thresholds;
    }

    public static Pixel[] filter(Pixel[] pixelData) {
        Pixel[] resultData = new Pixel[pixelData.length];

        int totalPixels = pixelData.length;

        int[] thresholds = countThreshold(pixelData);

        double[] doubleThresholds = new double[3];
        for (int i = 0; i < 3; i++) {
            doubleThresholds[i] = thresholds[i] / 255.0;
            System.out.println(thresholds[i] + "!!!!!!!!!!!!!");
        }

        for (int i = 0; i < totalPixels; i++) {
            double[] colors = pixelData[i].getColors();
            double redBinary = (colors[0] > doubleThresholds[0] ? 1 : 0);
            double greenBinary = (colors[1] > doubleThresholds[1] ? 1 : 0);
            double blueBinary = (colors[2] > doubleThresholds[2] ? 1 : 0);
            resultData[i] = new Pixel(redBinary, greenBinary, blueBinary);
        }

        return resultData;
    }

}

