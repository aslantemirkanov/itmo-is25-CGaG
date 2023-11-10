package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Pixel;

import java.util.HashSet;
import java.util.Set;

public class Atkinson extends DitheringAlgorithm {
    Set<Double> set = new HashSet<>();

    @Override
    void apply(Pixel[] pixels, String format, int bit, int w, int h) {
        int pointer;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                pointer = y * w + x;
                Pixel pixel = pixels[pointer];
                double[] values = new double[]{0, 0};
                double[] b = bits[bit - 1];
                for (int c = 0; c < 3; c++) {
                    for (int t = 0; t < b.length; t++) {
                        if (b[t] >= pixel.getColors()[c]) {
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
                    double newValue = (Math.abs(pixel.getColors()[c] - values[0]) <
                            Math.abs(pixel.getColors()[c] - values[1])) ? values[0] : values[1];
                    set.add(newValue);
                    pixels[pointer].setAt(c, newValue);

                    double error = pixel.getColors()[c] - newValue;
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
                            double newV = neighbourPixel.getColors()[c] + error * fraction;
                            neighbourPixel.setAt(c, newV);

                        }
                    }
                }
            }
        }
        System.out.println("УНИКАЛЬНЫХ ЦВЕТОВ: " + set.size());
    }
}

