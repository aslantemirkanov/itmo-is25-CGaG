package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.gamma.GammaCorrection;

public class Ordered extends DitheringAlgorithm {

    @Override
    public void apply(Pixel[] pixels, String format, int bit, int w, int h, double gamma) {
        int pointer = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Pixel pixel = pixels[pointer++];
                double[][] values = new double[][]{{0, 0}, {0, 0}, {0, 0}};
                double[] b = bits[bit - 1];
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

                    double gammaThreshold = GammaCorrection.doubleToLineal(thresholdMap[i % 8][j % 8], gamma);

                    if (pixel.getColors()[c] >= gammaThreshold) {
                        pixel.getColors()[c] = values[c][1];
                    } else {
                        pixel.getColors()[c] = values[c][0];
                    }
                }
            }
        }
    }

}
