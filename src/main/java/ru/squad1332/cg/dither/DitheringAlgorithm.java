package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.Pixel;

public interface DitheringAlgorithm {
    void apply(Pixel[] picture, int bit, int w, int h);
}
