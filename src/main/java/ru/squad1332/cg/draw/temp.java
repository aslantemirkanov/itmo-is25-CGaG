/*
package ru.squad1332.cg.draw;

import ru.squad1332.cg.entities.Pixel;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class temp {

    public static void drawLine(Pixel[] pixels, int w, int h, double x0, double y0, double x1, double y1, int width, double transparency, Pixel pixel) {
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

        public static void drawLine(Pixel[] pixels, int w, int h, double x0, double y0, double x1, double y1, double width, double transparency, Pixel pixel){
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

        double length = Math.sqrt(dx * dx + dy * dy);
        dx /= length;
        dy /= length;

        // Вычисляем векторы для смещения на ширину линии
        double offsetDX = -dy * width / 2.0;
        double offsetDY = dx * width / 2.0;

        // Начальная и конечная точки линии с учетом смещения
        double xStart = x0 + offsetDX;
        double yStart = y0 + offsetDY;
        double xEnd = x1 + offsetDX;
        double yEnd = y1 + offsetDY;

        // Вычисляем количество шагов для рисования линии
        int steps = (int) Math.ceil(length);

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double x = xStart + (xEnd - xStart) * t;
            double y = yStart + (yEnd - yStart) * t;

            // Рисуем пиксель с учетом прозрачности и цвета
            plot(pixels, h, w, (int) Math.round(x), (int) Math.round(y), transparency, pixel);
        }
    }




}
*/
