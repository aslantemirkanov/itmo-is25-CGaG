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
                pixels[pointer] = new Pixel(newValue, newValue, newValue);

                double error = pixel.getFirst() - newValue;
                if (x + 1 < w) {
                    Pixel rightPixel = pixels[pointer + 1];
                    double newPixel = Math.min(Math.max(0, rightPixel.getFirst() + error * 7 / 16), 1);
                    rightPixel.setFirst(newPixel);
                    rightPixel.setSecond(newPixel);
                    rightPixel.setThird(newPixel);
                }

                if (y + 1 < h) {
                    if (x > 0) {
                        Pixel bottomLeftPixel = pixels[pointer + w - 1];
                        double newPixel = Math.min(Math.max(0, bottomLeftPixel.getFirst() + error * 3 / 16), 1);
                        bottomLeftPixel.setFirst(newPixel);
                        bottomLeftPixel.setSecond(newPixel);
                        bottomLeftPixel.setThird(newPixel);
                    }

                    Pixel bottomPixel = pixels[pointer + w];
                    double newPixel = Math.min(Math.max(0, bottomPixel.getFirst() + error * 5 / 16), 1);
                    bottomPixel.setFirst(newPixel);
                    bottomPixel.setSecond(newPixel);
                    bottomPixel.setThird(newPixel);

                    if (x + 1 < w) {
                        Pixel bottomRightPixel = pixels[pointer + w + 1];
                        newPixel = Math.min(Math.max(0, bottomRightPixel.getFirst() + error * 1 / 16), 1);
                        bottomRightPixel.setFirst(newPixel);
                        bottomRightPixel.setSecond(newPixel);
                        bottomRightPixel.setThird(newPixel);
                    }
                }
            }
        }
    }
}
