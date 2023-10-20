package ru.squad1332.cg.gamma;

import ru.squad1332.cg.entities.Pixel;

public class GammaCorrection {


    public static Pixel[] convertGamma(Pixel[] pixelData, double curGamma, double newGamma) {
        pixelData = removeGamma(pixelData, curGamma);
        pixelData = assignGamma(pixelData, newGamma);
        return pixelData;
    }

    private static Pixel[] removeGamma(Pixel[] pixelData, double gamma) {
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getFirst();
            double g = color.getSecond();
            double b = color.getThird();
            if (gamma == 0) {
                r = srgbToLineal(r);
                g = srgbToLineal(g);
                b = srgbToLineal(b);
            } else {
                r = Math.pow(r, 1.0 / gamma);
                g = Math.pow(g, 1.0 / gamma);
                b = Math.pow(b, 1.0 / gamma);
            }
            pixelData[i] = new Pixel(r, g, b);
        }
        return pixelData;
    }

    private static Pixel[] assignGamma(Pixel[] pixelData, double gamma) {
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getFirst();
            double g = color.getSecond();
            double b = color.getThird();
            if (gamma == 0) {
                r = linealToSRGB(r);
                g = linealToSRGB(g);
                b = linealToSRGB(b);
            } else {
                r = Math.pow(r, gamma);
                g = Math.pow(g, gamma);
                b = Math.pow(b, gamma);
            }
            pixelData[i] = new Pixel(r, g, b);
        }
        return pixelData;
    }

    private static double srgbToLineal(double channel) {
        if (channel <= 0.04045) {
            return channel / 12.92;
        } else {
            return Math.pow(((channel + 0.055) / 1.055), 2.4);
        }
    }

    private static double linealToSRGB(double channel) {
        if (channel < 0.0031308) {
            return channel * 12.92;
        } else {
            return 1.055 * Math.pow(channel, 1 / 2.4) - 0.55;
        }
    }
}
