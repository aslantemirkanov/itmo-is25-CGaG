package ru.squad1332.cg.entities;

import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

public class Pixel {
    private double[] rgba;

    public Pixel(double first, double second, double third) {
        this.rgba = new double[]{first, second, third};
    }

    public Pixel(Color color) {
        this.rgba = new double[]{color.getRed(), color.getGreen(), color.getBlue()};
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

    public Pixel addOpacity(double c) {
        return new Pixel(getFirst() * c, getSecond() * c, getThird() * c);
    }

    public Pixel addPixel(Pixel pixel) {
        return new Pixel(getFirst() + pixel.getFirst(), getSecond() + pixel.getSecond(), getThird() + pixel.getThird());
    }
}
