package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.gamma.GammaCorrection;

import java.security.PublicKey;
import java.util.concurrent.ThreadLocalRandom;

public class Random extends DitheringAlgorithm {
    @Override
    public void apply(Pixel[] pixels, String format, int bit, int w, int h, double gamma) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double[] b = bits[bit - 1];
        for (int index = 0; index < w * h; index++) {
            Pixel pixel = pixels[index];
            double[][] values = new double[][]{{0, 0}, {0, 0}, {0, 0}};
            double first = -10;
            for (int c = 0; c < 3; c++) {
                pixel.getColors()[c] = GammaCorrection.doubleFromLineal(pixel.getColors()[c], gamma);
                for (int t = 0; t < b.length; t++) {
                    if (b[t] >= pixel.getColors()[c]) {
                        if (t == 0) {
                            values[c][0] = b[t];
                            values[c][1] = b[t];
                        } else {
                            values[c][0] = b[t - 1];
                            values[c][1] = b[t];
                        }
                        break;
                    }
                }

                pixel.getColors()[c] = GammaCorrection.doubleToLineal(pixel.getColors()[c], gamma);

                if (c == 0) {
                    double minRange = GammaCorrection.doubleToLineal(values[c][0], gamma);
                    double maxRange = GammaCorrection.doubleToLineal(values[c][1],gamma);

                    double randomThreshold = minRange + (maxRange - minRange) * random.nextDouble();

                    if (pixel.getColors()[c] >= randomThreshold) {
                        pixel.getColors()[c] = GammaCorrection.doubleToLineal(values[c][1], gamma);
                        //pixel.getColors()[c] = values[c][1];
                    } else {
                        pixel.getColors()[c] = GammaCorrection.doubleToLineal(values[c][0], gamma);
                        //pixel.getColors()[c] = values[c][0];
                    }
                    first = pixel.getColors()[c];

                } else {
                    if ("P5".equals(format)) {
                        pixel.getColors()[c] = first;
                    } else {
                        double minRange = GammaCorrection.doubleToLineal(values[c][0], gamma);
                        double maxRange = GammaCorrection.doubleToLineal(values[c][1],gamma);

                        double randomThreshold = minRange + (maxRange - minRange) * random.nextDouble();
                        if (pixel.getColors()[c] >= randomThreshold) {
                            pixel.getColors()[c] = GammaCorrection.doubleToLineal(values[c][1], gamma);
                        } else {
                            pixel.getColors()[c] = GammaCorrection.doubleToLineal(values[c][0], gamma);
                        }

                    }
                }

                //pixel.getColors()[c] = GammaCorrection.doubleToLineal(pixel.getColors()[c], gamma);

            }
        }
    }
}
