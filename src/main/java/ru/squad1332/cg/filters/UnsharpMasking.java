package ru.squad1332.cg.filters;

import ru.squad1332.cg.entities.Pixel;

public class UnsharpMasking {

    public static Pixel[] filter(Pixel[] pixelData, int width, int height, double amount, double sigma, int threshold) {
        Pixel[] blurredData = GaussFilter.filter(pixelData, width, height, sigma);

        Pixel[] differenceData = calculateDifference(pixelData, blurredData, threshold);

        return applyUnsharpMasking(pixelData, differenceData, amount);
    }

    private static Pixel[] calculateDifference(Pixel[] originalData, Pixel[] blurredData, int threshold) {
        Pixel[] differenceData = new Pixel[originalData.length];
        double doubleThreshold = threshold / 255.0;

        for (int i = 0; i < originalData.length; i++) {
            double diffR = originalData[i].getFirst() - blurredData[i].getFirst();
            double diffG = originalData[i].getSecond() - blurredData[i].getSecond();
            double diffB = originalData[i].getThird() - blurredData[i].getThird();

            diffR = (Math.abs(diffR) > doubleThreshold) ? diffR : 0;
            diffG = (Math.abs(diffG) > doubleThreshold) ? diffG : 0;
            diffB = (Math.abs(diffB) > doubleThreshold) ? diffB : 0;

            differenceData[i] = new Pixel(diffR, diffG, diffB);
        }

        return differenceData;
    }

    private static Pixel[] applyUnsharpMasking(Pixel[] originalData, Pixel[] differenceData, double amount) {
        Pixel[] resultData = new Pixel[originalData.length];

        for (int i = 0; i < originalData.length; i++) {
            double enhancedR = originalData[i].getFirst() + (amount * differenceData[i].getFirst());
            double enhancedG = originalData[i].getSecond() + (amount * differenceData[i].getSecond());
            double enhancedB = originalData[i].getThird() + (amount  * differenceData[i].getThird());

            enhancedR = clamp(enhancedR, 0, 1);
            enhancedG = clamp(enhancedG, 0, 1);
            enhancedB = clamp(enhancedB, 0, 1);

            resultData[i] = new Pixel(enhancedR, enhancedG, enhancedB);
        }

        return resultData;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
