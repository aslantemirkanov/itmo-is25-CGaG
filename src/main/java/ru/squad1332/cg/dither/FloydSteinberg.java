package ru.squad1332.cg.dither;

import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.gamma.GammaCorrection;

public class FloydSteinberg extends DitheringAlgorithm {
    @Override
    void apply(Pixel[] pixels, String format, int bit, int w, int h, double gamma) {
        int pointer;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                pointer = y * w + x;
                Pixel pixel = pixels[pointer];

                double[] values = new double[]{0, 0};
                double[] b = bits[bit - 1];

                for (int c = 0; c < 3; c++) {
                    pixel.getColors()[c] = GammaCorrection.doubleFromLineal(pixel.getColors()[c], gamma);
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

                    newValue = GammaCorrection.doubleToLineal(newValue, gamma);
                    pixel.getColors()[c] = GammaCorrection.doubleToLineal(pixel.getColors()[c], gamma);

                    double error = pixel.getColors()[c] - newValue;

                    pixels[pointer].setAt(c, newValue);

                    //newValue = GammaCorrection.doubleFromLineal(newValue, gamma);



                    //error = GammaCorrection.doubleFromLineal(error, gamma);

                    //newValue = GammaCorrection.doubleFromLineal(newValue, gamma);
                    //pixel.getColors()[c] = GammaCorrection.doubleFromLineal(pixel.getColors()[c], gamma);

                    //GammaCorrection.pixelFromLineal(pixel, gamma);

                    if (x + 1 < w) {
                        Pixel rightPixel = pixels[pointer + 1];
                        //GammaCorrection.pixelToLineal(rightPixel, gamma);
                        double newPixel = Math.min(Math.max(0, rightPixel.getColors()[c] + error * 7 / 16), 1);
                        rightPixel.setAt(c, newPixel);
                        //GammaCorrection.pixelFromLineal(rightPixel, gamma);
                    }

                    if (y + 1 < h) {
                        if (x > 0) {
                            Pixel bottomLeftPixel = pixels[pointer + w - 1];
                            //GammaCorrection.pixelToLineal(bottomLeftPixel, gamma);
                            double newPixel = Math.min(Math.max(0, bottomLeftPixel.getColors()[c] + error * 3 / 16), 1);
                            bottomLeftPixel.setAt(c, newPixel);
                            //GammaCorrection.pixelFromLineal(bottomLeftPixel, gamma);
                        }

                        Pixel bottomPixel = pixels[pointer + w];
                        //GammaCorrection.pixelToLineal(bottomPixel, gamma);
                        double newPixel = Math.min(Math.max(0, bottomPixel.getColors()[c] + error * 5 / 16), 1);
                        bottomPixel.setAt(c, newPixel);
                        //GammaCorrection.pixelFromLineal(bottomPixel, gamma);

                        if (x + 1 < w) {
                            Pixel bottomRightPixel = pixels[pointer + w + 1];
                            //GammaCorrection.pixelToLineal(bottomRightPixel, gamma);
                            newPixel = Math.min(Math.max(0, bottomRightPixel.getColors()[c] + error * 1 / 16), 1);
                            bottomRightPixel.setAt(c, newPixel);
                            //GammaCorrection.pixelFromLineal(bottomRightPixel, gamma);
                        }
                    }
//                    pixel = GammaCorrection.pixelFromLineal(pixel, gamma);
                }
            }
        }
    }
}
