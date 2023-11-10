package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Pixel;

public class Atkinson extends DitheringAlgorithm {
    @Override
    void apply(Pixel[] pixels, int bit, int w, int h) {
        int pointer;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                pointer = y * w + x;
                Pixel pixel = pixels[pointer];
                double[] values = new double[]{0, 0};
                double[] b = bits[bit - 1];
                for (int t = 0; t < b.length; t++) {
                    if (b[t] >= pixel.getColors()[0]) {
                        if (t == 0) {
                            values[0] = b[t];
                            values[1] = b[t];
                        } else {
                            values[0] = b[t - 1];
                            values[1] = b[t];
                        }
                        break;
                    }
                }
                double newValue = (Math.abs(pixel.getFirst() - values[0]) < Math.abs(pixel.getFirst() - values[1])) ? values[0] : values[1];
                pixels[pointer] = new Pixel(newValue, newValue, newValue);

                double error = pixel.getFirst() - newValue;
                int[][] offset = {
                        {1, 0}, {2, 0},
                        {-1, 1}, {0, 1}, {1, 1},
                        {0, 2}
                };

                double fraction = 1.0 / 8;
                for (int[] o : offset) {
                    int nx = x + o[0];
                    int ny = y + o[1];

                    if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                        Pixel neighbourPixel = pixels[ny * w + nx];
                        double newV = neighbourPixel.getFirst() + error * fraction;
                        neighbourPixel.setFirst(newV);
                        neighbourPixel.setSecond(newV);
                        neighbourPixel.setThird(newV);
                    }
                }
            }
        }
    }
}
