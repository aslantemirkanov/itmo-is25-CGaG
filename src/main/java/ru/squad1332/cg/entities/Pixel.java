package ru.squad1332.cg.entities;

import java.util.Arrays;

public class Pixel {
    private double[] rgba;

    public Pixel(double red, double green, double blue, double alpha) {
        this.rgba = new double[]{red, green, blue, alpha};
    }

    public double[] getRgba() {
        return rgba;
    }

    public void setRgb(double[] rgba) {
        if (rgba.length != 4) {
            for (double i : rgba) {
                if (i < 0 || i > 1) {
                    throw new IllegalArgumentException("Expected array of rgba, but get: " + Arrays.toString(rgba));
                }
            }
        }
        this.rgba = rgba;
    }

    public double getRed() {
        return rgba[0];
    }

    public double getGreen() {
        return rgba[1];
    }

    public double getBlue() {
        return rgba[2];
    }

    public double getAlpha() {
        return rgba[3];
    }
}
