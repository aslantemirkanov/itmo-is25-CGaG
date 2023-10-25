package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Pixel;

import java.util.concurrent.ThreadLocalRandom;

public class Random extends DitheringAlgorithm {
    @Override
    public void apply(Pixel[] pixels, int bit, int w, int h) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int pointer = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Pixel pixel = pixels[pointer++];
                double[][] values = new double[][]{{0, 0}, {0, 0}, {0, 0}};
                double[] b = bits[bit - 1];
                for (int t = 0; t < b.length; t++) {
                    if (b[t] >= pixel.getColors()[0]) {
                        if (t == 0) {
                            values[0][0] = b[t];
                            values[0][1] = b[t];
                        } else {
                            values[0][0] = b[t - 1];
                            values[0][1] = b[t];
                        }
                        break;
                    }
                }
                double randomThreshold = random.nextDouble();
                if (pixel.getColors()[0] >= randomThreshold) {
                    pixel.getColors()[0] = values[0][1];
                    pixel.getColors()[1] = values[0][1];
                    pixel.getColors()[2] = values[0][1];
                } else {
                    pixel.getColors()[0] = values[0][0];
                    pixel.getColors()[1] = values[0][0];
                    pixel.getColors()[2] = values[0][0];
                }

            }
        }
    }
}
