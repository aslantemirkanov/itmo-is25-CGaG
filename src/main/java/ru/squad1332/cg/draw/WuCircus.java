package ru.squad1332.cg.draw;

import ru.squad1332.cg.entities.Pixel;

import static java.lang.Math.*;

public class WuCircus {

    static int ipart(double x) {
        return (int) x;
    }

    static double fpart(double x) {
        return x - floor(x);
    }

    static double rfpart(double x) {
        return 1 - fpart(x);
    }
    static boolean steep;

    public static void drawLine(Pixel[] pixels, int w, int h, double x0, double y0, double x1, double y1, double width, double transparency, Pixel pixel) {
        // width is now a double, representing the fractional pixel width of the line
        steep = abs(y1 - y0) > abs(x1 - x0);
        double temp;


        // делаем линию такой чтобы
        if (steep) {
            // Swap x and y
            temp = x0;
            x0 = y0;
            y0 = temp;
            temp = x1;
            x1 = y1;
            y1 = temp;
        }

        if (x0 > x1) {
            // Swap ends of the line
            temp = x0;
            x0 = x1;
            x1 = temp;
            temp = y0;
            y0 = y1;
            y1 = temp;
        }

        double[][] transparencyBuffer = new double[w][h]; // w and h are the width and height of the pixel array

        // Initialize the buffer with zeros
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                transparencyBuffer[i][j] = 0;
            }
        }


        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = dy / dx;

        // Handle start of the line
        double xend = round(x0);
        double yend = y0 + gradient * (xend - x0);
        double xgap = rfpart(x0 + 0.5);
        int xpxl1 = (int) xend; // this will be used in the main loop
        int ypxl1 = ipart(yend);
        if (steep) {
            plot(pixels, w, h, ypxl1, xpxl1, rfpart(yend) * xgap * transparency, width, pixel, transparencyBuffer);
            plot(pixels, w, h, ypxl1 + 1, xpxl1, fpart(yend) * xgap * transparency, width, pixel, transparencyBuffer);
        } else {
            plot(pixels, w, h, xpxl1, ypxl1, rfpart(yend) * xgap * transparency, width, pixel, transparencyBuffer);
            plot(pixels, w, h, xpxl1, ypxl1 + 1, fpart(yend) * xgap * transparency, width, pixel, transparencyBuffer);
        }
        double intery = yend + gradient; // first y-intersection for the main loop

        // Handle end of the line
        xend = round(x1);
        yend = y1 + gradient * (xend - x1);
        xgap = fpart(x1 + 0.5);
        int xpxl2 = (int) xend; // this will be used in the main loop
        int ypxl2 = ipart(yend);
        if (steep) {
            plot(pixels, w, h, ypxl2, xpxl2, rfpart(yend) * xgap * transparency, width, pixel, transparencyBuffer);
            plot(pixels, w, h, ypxl2 + 1, xpxl2, fpart(yend) * xgap * transparency, width, pixel, transparencyBuffer);
        } else {
            plot(pixels, w, h, xpxl2, ypxl2, rfpart(yend) * xgap * transparency, width, pixel, transparencyBuffer);
            plot(pixels, w, h, xpxl2, ypxl2 + 1, fpart(yend) * xgap * transparency, width, pixel, transparencyBuffer);
        }

        // Main loop
        if (steep) {
            for (int x = xpxl1 + 1; x < xpxl2; x++) {
                plot(pixels, w, h, ipart(intery), x, rfpart(intery) * transparency, width, pixel, transparencyBuffer);
                plot(pixels, w, h, ipart(intery) + 1, x, fpart(intery) * transparency, width, pixel, transparencyBuffer);
                intery += gradient;
            }
        } else {
            for (int x = xpxl1 + 1; x < xpxl2; x++) {
                plot(pixels, w, h, x, ipart(intery), rfpart(intery) * transparency, width, pixel, transparencyBuffer);
                plot(pixels, w, h, x, ipart(intery) + 1, fpart(intery) * transparency, width, pixel, transparencyBuffer);
                intery += gradient;
            }
        }
    }

    // Modified plot function to handle fractional widths
    static void plot(Pixel[] pixels, int w, int h, int x, int y, double brightness, double width, Pixel pixel, double[][] transparencyBuffer) {
        // Calculate the range of pixels affected by the width
        int xStart = (int) Math.max(x - (width - 1) / 2, 0);
        int xEnd = (int) Math.min(x + (width + 1) / 2, w - 1);
        int yStart = (int) Math.max(y - (width - 1) / 2, 0);
        int yEnd = (int) Math.min(y + (width + 1) / 2, h - 1);
        double eps = 0.00001;

        for (int ix = xStart; ix <= xEnd; ix++) {
            for (int iy = yStart; iy <= yEnd; iy++) {
                double distX = ix - x;
                double distY = iy - y;
                double dist = sqrt(distX * distX + distY * distY);
                double pixelWidth = width / 2.0;
                double edgeDistance = pixelWidth - dist;
                double pixelBrightness = edgeDistance > 0 ? brightness : brightness * (1 - min(1, -edgeDistance));
                int index = w * iy + ix;
                Pixel p = pixels[index];
                pixels[index] = pixel.addOpacity(pixelBrightness).addPixel(p.addOpacity(1 - pixelBrightness));
            }
        }
    }
}