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

    public double getSecond() {
        return rgba[1];
    }

    public double getThird() {
        return rgba[2];
    }

}
