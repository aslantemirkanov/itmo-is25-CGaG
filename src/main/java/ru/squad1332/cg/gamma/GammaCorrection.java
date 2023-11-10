package ru.squad1332.cg.gamma;

import ru.squad1332.cg.entities.Pixel;

public class GammaCorrection {


    public static Pixel[] convertGamma(Pixel[] pixelData, double curGamma, double newGamma) {
        if (curGamma!= newGamma){
            pixelData = removeGamma(pixelData, curGamma);
            pixelData = assignGamma(pixelData, newGamma);
        }
        return pixelData;
    }

    public static Pixel[] removeGamma(Pixel[] pixelData, double gamma) {
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getFirst();
            double g = color.getSecond();
            double b = color.getThird();
            if (gamma == 0) {
                pixelData[i].setFirst(srgbToLineal(r));
                pixelData[i].setSecond(srgbToLineal(g));
                pixelData[i].setThird(srgbToLineal(b));
            } else {
                pixelData[i].setFirst(Math.pow(r, 1.0 / gamma));
                pixelData[i].setSecond(Math.pow(g, 1.0 /  gamma));
                pixelData[i].setThird(Math.pow(b, 1.0 /  gamma));
            }
        }
        return pixelData;
    }

    public static Pixel[] assignGamma(Pixel[] pixelData, double gamma) {
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getFirst();
            double g = color.getSecond();
            double b = color.getThird();
            if (gamma == 0) {
                pixelData[i].setFirst(linealToSRGB(r));
                pixelData[i].setSecond(linealToSRGB(g));
                pixelData[i].setThird(linealToSRGB(b));
            } else {
                pixelData[i].setFirst(Math.pow(r, gamma));
                pixelData[i].setSecond(Math.pow(g, gamma));
                pixelData[i].setThird(Math.pow(b, gamma));
            }
        }
        return pixelData;
    }

    public static double srgbToLineal(double channel) {
        double res;
        if (channel <= 0.04045) {
            res = channel / 12.92;
        } else {
            res = Math.pow(((channel + 0.055) / 1.055), 2.4);
        }
        return res;
    }

    public static double linealToSRGB(double channel) {
        double res;
        if (channel <= 0.0031308) {
            res = channel * 12.92;
        } else {
            res = 1.055 * Math.pow(channel, 1.0 / 2.4) - 0.055;
        }
        return res;
    }
}
