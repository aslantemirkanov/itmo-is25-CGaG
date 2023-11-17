package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.gamma.GammaCorrection;

import java.util.Map;

public class DitheringService {
    private static final Map<String, DitheringAlgorithm> DITHER_BY_NAME = Map.of("Ordered", new Ordered(),
            "Random", new Random(),
            "Floyd-Steinberg", new FloydSteinberg(),
            "Atkinson", new Atkinson());
    public static void applyDithering(Pixel[] picture, String mode, String format, int bit, int w, int h, double gamma) {
        picture = GammaCorrection.removeGamma(picture, gamma);
        System.out.println(gamma);
        DITHER_BY_NAME.get(mode).apply(picture, format, bit, w, h, gamma);
        picture = GammaCorrection.assignGamma(picture, gamma);
    }
}
