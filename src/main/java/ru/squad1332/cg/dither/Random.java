package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Pixel;

import java.util.concurrent.ThreadLocalRandom;

public class Random extends DitheringAlgorithm {
    @Override
    public void apply(Pixel[] pixels, String format, int bit, int w, int h) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int pointer = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Pixel pixel = pixels[pointer++];
                double[][] values = new double[][]{{0, 0}, {0, 0}, {0, 0}};
                double[] b = bits[bit - 1];
                double first = -10;
                for (int c = 0; c < 3; c++) {
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
                    if (c == 0) {
                        double randomThreshold = random.nextDouble();
                        if (pixel.getColors()[c] >= randomThreshold) {
                            pixel.getColors()[c] = values[c][1];
                        } else {
                            pixel.getColors()[c] = values[c][0];
                        }
                        first = pixel.getColors()[c];

                    } else {
                        if ("P5".equals(format)) {
                            pixel.getColors()[c] = first;
                        } else {
                            double randomThreshold = random.nextDouble();
                            if (pixel.getColors()[c] >= randomThreshold) {
                                pixel.getColors()[c] = values[c][1];
                            } else {
                                pixel.getColors()[c] = values[c][0];
                            }

                        }
                    }

                }
            }
        }
    }
}
