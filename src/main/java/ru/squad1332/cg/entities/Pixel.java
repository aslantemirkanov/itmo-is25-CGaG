package ru.squad1332.cg.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Pixel {
    private int[] rgb;

    public Pixel(int red, int green, int blue) {
        rgb = new int[]{red, green, blue};
    }
}
