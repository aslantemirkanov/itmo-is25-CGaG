//как в пэинте
/*package ru.squad1332.cg.draw;

import ru.squad1332.cg.entities.Pixel;

import static java.lang.Math.*;

public class Wu {

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
}*/
/*
    static void plot(Pixel[] pixels, int w, int h, int x, int y, double brightness, double width, Pixel pixel, double angle) {
        // Calculate the range of pixels affected by the width
        int xStart = (int) Math.max(x - (width - 1) / 2, 0);
        int xEnd = (int) Math.min(x + (width + 1) / 2, w - 1);
        int yStart = (int) Math.max(y - (width - 1) / 2, 0);
        int yEnd = (int) Math.min(y + (width + 1) / 2, h - 1);

        // Calculate sine and cosine of the angle
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);


        for (int ix = xStart; ix <= xEnd; ix++) {
            for (int iy = yStart; iy <= yEnd; iy++) {
                // Calculate the coordinates relative to the center point (x, y)
                double dx = ix - x;
                double dy = iy - y;

                // Rotate the coordinates by the specified angle to get the perpendicular line
                double rotatedX = cosAngle * dx - sinAngle * dy;
                double rotatedY = sinAngle * dx + cosAngle * dy;

                // Calculate the distance from the center along the perpendicular line
                double dist = Math.abs(rotatedY);

                double pixelWidth = width / 2.0;
                double edgeDistance = pixelWidth - dist;
                double pixelBrightness = edgeDistance > 0 ? brightness : brightness * (1 - Math.min(1, -edgeDistance));

                int index = w * iy + ix;
                Pixel p = pixels[index];
                pixels[index] = pixel.addOpacity(pixelBrightness).addPixel(p.addOpacity(1 - pixelBrightness));
            }
        }
    }
*/

//sanya

package ru.squad1332.cg.draw;

import ru.squad1332.cg.entities.Pixel;

import java.util.Arrays;

import static java.lang.Math.*;

public class Wu {
    static int ipart(double x) {
        return (int) x;
    }

    static double fpart(double x) {
        return x - floor(x);
    }

    static double rfpart(double x) {
        return 1.0 - fpart(x);
    }

    static double buffer[];

    public static void drawLine(Pixel[] pixels, int w, int h, double x0, double y0, double x1, double y1, double width, double transparency, Pixel pixel) {
        System.out.println("width!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + width);
        buffer = new double[w * h];
        Arrays.fill(buffer, 0);
        if (width > 2) {
            double angle = Math.atan2(y1 - y0, x1 - x0);

            double dx = (width / 2) * Math.sin(angle);
            double dy = (width / 2) * Math.cos(angle);

            double Ax = min(w, max(0, x0 - dx));
            double Ay = min(h, max(0, y0 + dy));
            double Bx = min(w, max(0, x1 - dx));
            double By = min(h, max(0, y1 + dy));
            double Cx = min(w, max(0, x1 + dx));
            double Cy = min(h, max(0, y1 - dy));
            double Dx = min(w, max(0, x0 + dx));
            double Dy = min(h, max(0, y0 - dy));

            fillRectangle(pixels, w, h, Ax, Ay, Bx, By, Cx, Cy, Dx, Dy, pixel, transparency);

            drawSingleLine(pixels, w, h, Ax, Ay, Bx, By, transparency, pixel);
            drawSingleLine(pixels, w, h, Bx, By  , Cx  , Cy  , transparency, pixel);
            drawSingleLine(pixels, w, h, Cx, Cy  , Dx  , Dy  , transparency, pixel);
            drawSingleLine(pixels, w, h, Dx, Dy  , Ax  , Ay  , transparency, pixel);

        } else {
            drawSingleLine(pixels, w, h, x0, y0, x1, y1, transparency, pixel);
        }
    }


    public static void fillRectangle(Pixel[] pixels, int imageWidth, int imageHeight,
                                     double Ax, double Ay, double Bx, double By,
                                     double Cx, double Cy, double Dx, double Dy,
                                     Pixel pixel, double transparency) {
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                if (isPointInRectangle(x, y, Ax, Ay, Bx, By, Cx, Cy, Dx, Dy)) {
                    plot(pixels, imageWidth, imageHeight, x, y, pixel, transparency);
                }
            }
        }
    }

    private static void plot(Pixel[] pixels, int imageWidth, int imageHeight, int x, int y, Pixel pixel, double transparency) {
        int index = y * imageWidth + x;
        if (index < 0 || index >= pixels.length) {
            return;
        }

        buffer[index] = transparency;
        double newTransparency = transparency;

        Pixel p = pixels[index];
        pixels[index] = pixel.addOpacity(newTransparency).addPixel(p.addOpacity(1 - newTransparency));
    }

    private static boolean isPointInRectangle(double px, double py, double Ax, double Ay, double Bx, double By, double Cx, double Cy, double Dx, double Dy) {
        boolean b1 = sign(px, py, Ax, Ay, Bx, By) < 0.0f;
        boolean b2 = sign(px, py, Bx, By, Cx, Cy) < 0.0f;
        boolean b3 = sign(px, py, Cx, Cy, Dx, Dy) < 0.0f;
        boolean b4 = sign(px, py, Dx, Dy, Ax, Ay) < 0.0f;

        return ((b1 == b2) && (b2 == b3) && (b3 == b4));
    }

    private static double sign(double px, double py, double Ax, double Ay, double Bx, double By) {
        return (px - Bx) * (Ay - By) - (Ax - Bx) * (py - By);
    }


    public static void drawSingleLine(Pixel[] pixels, int w, int h, double x0, double y0, double x1, double y1, double transparency, Pixel pixel) {
        double width = 1;
        System.out.println(w);
        boolean steep = abs(y1 - y0) > abs(x1 - x0);
        double tmp;
        if (steep) {
            tmp = x0;
            x0 = y0;
            y0 = tmp;
            tmp = x1;
            x1 = y1;
            y1 = tmp;
        }
        if (x0 > x1) {
            tmp = x0;
            x0 = x1;
            x1 = tmp;
            tmp = y0;
            y0 = y1;
            y1 = tmp;
        }


        double dx = x1 - x0;
        double dy = y1 - y0;
        double angleRadians = Math.atan2(dy, dx);
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        double gradient = 0;
        if (dx == 0) {
            gradient = 1;
        } else {
            gradient = dy / dx;
        }

        width = (int) (width * Math.sqrt(1 + (gradient * gradient)));


        double xEnd = round(x0);
        double yEnd = y0 - (width - 1) * 0.5 + gradient * (xEnd - x0);
        double xGap = rfpart(x0 + 0.5);
        double xPxl1 = xEnd;
        double yPxl1 = ipart(yEnd);
        double intersectY = yEnd + gradient;

        xEnd = round(x1);
        yEnd = y1 - (width - 1) * 0.5 + gradient * (xEnd - x1);
        xGap = fpart(x1 + 0.5);

        double xPxl2 = xEnd;
        double yPxl2 = ipart(yEnd);


        if (steep) {
            for (double x = xPxl1; x <= xPxl2; x++) {
                plot(pixels, h, w, ipart(intersectY), x, rfpart(intersectY) * transparency, pixel);
/*                for (int i = 1; i < width; i++) {
                    plot(pixels, h, w, ipart(intersectY) + i, x, transparency, pixel);
                }*/
                plot(pixels, h, w, ipart(intersectY) + width, x, fpart(intersectY) * transparency, pixel);
                intersectY += gradient;
            }
        } else {
            for (double x = xPxl1; x <= xPxl2; x++) {
                plot(pixels, h, w, x, ipart(intersectY), rfpart(intersectY) * transparency, pixel);
/*                for (int i = 1; i < width; i++) {
                    plot(pixels, h, w, x, ipart(intersectY) + i, transparency, pixel);
                }*/
                plot(pixels, h, w, x, ipart(intersectY) + width, fpart(intersectY) * transparency, pixel);
                intersectY += gradient;
            }
        }
    }

    public static void plot(Pixel[] pixels, int h, int w, double x, double y, double c, Pixel pixel) {
        int index = w * (int) y + (int) x;
        /*if (buffer[index] == 0) {
            Pixel p = pixels[index];
            pixels[index] = pixel.addOpacity(c).addPixel(p.addOpacity(1 - c));
        }*/
        Pixel p = pixels[index];
        pixels[index] = pixel.addOpacity(c).addPixel(p.addOpacity(1 - c));
    }

}

/*
package ru.squad1332.cg.draw;

import ru.squad1332.cg.entities.Pixel;

import static java.lang.Math.*;

public class Wu {
    static int ipart(double x) {
        return (int) x;
    }

    static double fpart(double x) {
        return x - floor(x);
    }

    static double rfpart(double x) {
        return 1.0 - fpart(x);
    }

    public static void drawLine(Pixel[] pixels, int w, int h, double x0, double y0, double x1, double y1, double width, double transparency, Pixel pixel) {
        System.out.println(w);
        boolean steep = abs(y1 - y0) > abs(x1 - x0);
        double tmp;
        if (steep) {
            tmp = x0;
            x0 = y0;
            y0 = tmp;
            tmp = x1;
            x1 = y1;
            y1 = tmp;
        }
        if (x0 > x1) {
            tmp = x0;
            x0 = x1;
            x1 = tmp;
            tmp = y0;
            y0 = y1;
            y1 = tmp;
        }


        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = 0;
        if (dx == 0) {
            gradient = 1;
        } else {
            gradient = dy / dx;
        }

        width = (int) (width * Math.sqrt(1 + (gradient * gradient)));

        double xEnd = round(x0);
        double yEnd = y0 - (width - 1) * 0.5 + gradient * (xEnd - x0);
        double xGap = rfpart(x0 + 0.5);
        double xPxl1 = xEnd;
        double yPxl1 = ipart(yEnd);
        if (steep) {
            plot(pixels, h, w, yPxl1, xPxl1, rfpart(yEnd) * xGap * transparency, pixel);
            for (int i = 1; i < width; i++) {
                plot(pixels, h, w, yPxl1 + i, xPxl1, fpart(yEnd) * xGap * transparency, pixel);
            }
            plot(pixels, h, w, yPxl1 + width, xPxl1, fpart(yEnd) * xGap * transparency, pixel);
        } else {
            plot(pixels, h, w, xPxl1, yPxl1, rfpart(yEnd) * xGap * transparency, pixel);
            for (int i = 1; i < width; i++) {
                plot(pixels, h, w, xPxl1, yPxl1 + i, fpart(yEnd) * xGap * transparency, pixel);
            }
            plot(pixels, h, w, xPxl1, yPxl1 + width, fpart(yEnd) * xGap * transparency, pixel);
        }

        double intersectY = yEnd + gradient;

        xEnd = round(x1);
        yEnd = y1 - (width - 1) * 0.5 + gradient * (xEnd - x1);
        xGap = fpart(x1 + 0.5);
        double xPxl2 = xEnd;
        double yPxl2 = ipart(yEnd);
        if (steep) {
            plot(pixels, h, w, yPxl2, xPxl2, rfpart(yEnd) * xGap * transparency, pixel);
            for (int i = 1; i < width; i++) {
                plot(pixels, h, w, yPxl2 + i, xPxl2, fpart(yEnd) * xGap * transparency, pixel);
            }
            plot(pixels, h, w, yPxl2 + width, xPxl2, fpart(yEnd) * xGap * transparency, pixel);
        } else {
            plot(pixels, h, w, xPxl2, yPxl2, rfpart(yEnd) * xGap * transparency, pixel);
            for (int i = 1; i < width; i++) {
                plot(pixels, h, w, xPxl2, yPxl2 + i, fpart(yEnd) * xGap * transparency, pixel);
            }
            plot(pixels, h, w, xPxl2, yPxl2 + width, fpart(yEnd) * xGap * transparency, pixel);
        }
        if (steep) {
            for (double x = xPxl1; x <= xPxl2; x++) {
                plot(pixels, h, w, ipart(intersectY), x, rfpart(intersectY) * transparency, pixel);
                for (int i = 1; i < width; i++) {
                    plot(pixels, h, w, ipart(intersectY) + i, x, transparency, pixel);
                }
                plot(pixels, h, w, ipart(intersectY) + width, x, fpart(intersectY) * transparency, pixel);
                intersectY += gradient;
            }
        } else {
            for (double x = xPxl1; x <= xPxl2; x++) {
                plot(pixels, h, w, x, ipart(intersectY), rfpart(intersectY) * transparency, pixel);
                for (int i = 1; i < width; i++) {
                    plot(pixels, h, w, x, ipart(intersectY) + i, transparency, pixel);
                }
                plot(pixels, h, w, x, ipart(intersectY) + width, fpart(intersectY) * transparency, pixel);
                intersectY += gradient;
            }
        }
    }

    static void plot(Pixel[] pixels, int h, int w, double x, double y, double c, Pixel pixel) {
        int index = w * (int) y + (int) x;
        Pixel p = pixels[index];
        pixels[index] = pixel.addOpacity(c).addPixel(p.addOpacity(1 - c));
    }
}
*/
