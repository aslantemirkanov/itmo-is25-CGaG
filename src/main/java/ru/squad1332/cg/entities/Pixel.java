package ru.squad1332.cg.entities;

import java.util.Arrays;

public class Pixel {
    private double[] rgba;

    public Pixel(double first, double second, double third) {
        this.rgba = new double[]{first, second, third};
    }

    public double[] getColors() {
        return rgba;
    }

    public void setRgb(double[] rgba) {
        if (rgba.length != 3) {
            for (double i : rgba) {
                if (i < 0 || i > 1) {
                    throw new IllegalArgumentException("Expected array of rgba, but get: " + Arrays.toString(rgba));
                }
            }
        }
        this.rgba = rgba;
    }

    public double getFirst() {
        return rgba[0];
    }

    public void setFirst(double r) {
        rgba[0] = r;
    }

    public double getSecond() {
        return rgba[1];
    }

    public void setSecond(double g) {
        rgba[1] = g;
    }

    public double getThird() {
        return rgba[2];
    }

    public void setThird(double b) {
        rgba[2] = b;
    }
}
