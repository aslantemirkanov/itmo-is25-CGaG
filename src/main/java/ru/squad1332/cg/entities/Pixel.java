package ru.squad1332.cg.entities;

public class Pixel {
    private int[] rgb;

    public Pixel(int red, int green, int blue) {
        rgb = new int[]{red, green, blue};
    }

    public int[] getRgb() {
        return rgb;
    }

    public void setRgb(int[] rgb) {
        this.rgb = rgb;
    }
}
