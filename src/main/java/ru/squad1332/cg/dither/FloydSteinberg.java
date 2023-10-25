package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Pixel;

public class FloydSteinberg extends DitheringAlgorithm {
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

                double error = pixel.getFirst() - newValue;
                if (x + 1 < w) {
                    Pixel rightPixel = pixels[pointer + 1];
                    rightPixel.setFirst(error * 7 / 16);
                    rightPixel.setSecond(error * 7 / 16);
                    rightPixel.setThird(error * 7 / 16);
                }

                if (y + 1 < h) {
                    if (x > 0) {
                        Pixel bottomLeftPixel = pixels[pointer + w - 1];
                        bottomLeftPixel.setFirst(error * 3 / 16);
                        bottomLeftPixel.setSecond(error * 3 / 16);
                        bottomLeftPixel.setThird(error * 3 / 16);
                    }

                    Pixel bottomPixel = pixels[pointer + w];
                    bottomPixel.setFirst(error * 5 / 16);
                    bottomPixel.setSecond(error * 5 / 16);
                    bottomPixel.setThird(error * 5 / 16);

                    if (x + 1 < w) {
                        Pixel bottomRightPixel = pixels[pointer + w + 1];

                        bottomRightPixel.setFirst(error * 1 / 16);
                        bottomRightPixel.setSecond(error * 1 / 16);
                        bottomRightPixel.setThird(error * 1 / 16);
                    }
                }
            }
        }
    }
}
