package ru.squad1332.cg.convertor;

import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.YCbCrFormat;

import java.util.Map;

public class ColorConvertor {
    private static final Map<Channel, int[]> POSITIONS_MAP = Map.of(
            Channel.ALL, new int[]{1, 1, 1},
            Channel.FIRST, new int[]{1, 0, 0},
            Channel.SECOND, new int[]{0, 1, 0},
            Channel.THIRD, new int[]{0, 0, 1}
    );

    public static Pixel[] convertRgbToRgb(Pixel[] pixelData, Channel channel) {
        Pixel[] result = new Pixel[pixelData.length];
        int[] pos = POSITIONS_MAP.get(channel);
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getFirst();
            double g = color.getSecond();
            double b = color.getThird();
            result[i] = new Pixel(pos[0] * r, pos[1] * g, pos[2] * b, color.getAlpha());
        }
        return result;
    }

    public static Pixel[] convertRgbToHsl(Pixel[] pixelData, Channel channel) {
        System.out.println("HSL ");
        Pixel[] result = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getFirst();
            double g = color.getSecond();
            double b = color.getThird();

            double max = Math.max(r, Math.max(g, b));
            double min = Math.min(r, Math.min(g, b));
            double d = max - min;
            double l = (max + min) / 2;
            double s = d / (1 - Math.abs(1 - (max + min)));
            double h = 0;

            if (max == min) {
                h = 0;
                s = 0;
            } else if (max == r && g >= b) {
                h = 60 * (g - b) / d;
            } else if (max == r && g < b) {
                h = 60 * (g - b) / d + 360;
            } else if (max == g) {
                h = 60 * (b - r) / d + 120;
            } else if (max == b) {
                h = 60 * (r - g) / d + 240;
            }


            switch (channel) {
                case ALL -> {
                    h = h;
                    s = s;
                    l = l;
                }
                case FIRST -> {
                    h = h;
                    s = 0.5; // средняя насыщенность
                    l = 0.5; // средняя яркость
                }
                case SECOND -> {
                    h = 0; // красный цвет - главное зафиксировать
                    s = s;
                    l = 0.5; // средняя яркость
                }
                case THIRD -> {
                    h = 0;
                    s = 0; // обнуляем насыщенность, чтобы цвет был чб
                    l = l;
                }
            }

            Pixel pixel = new Pixel(h, s, l, 1);
            result[i] = pixel;
        }

        return result;
    }

    public static Pixel[] convertHslToRgb(Pixel[] pixelData, Channel channel) {
        Pixel[] result = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double h = color.getFirst() * 360;
            double s = color.getSecond();
            double l = color.getThird();

            double c = (1.0 - Math.abs(2.0 * l - 1.0)) * s;
            double x = c * (1.0 - Math.abs((h / 60.0) % 2.0 - 1.0));
            double m = l - c / 2.0;

            double r, g, b;
            if (h >= 0 && h < 60) {
                r = c;
                g = x;
                b = 0;
            } else if (h >= 60 && h < 120) {
                r = x;
                g = c;
                b = 0;
            } else if (h >= 120 && h < 180) {
                r = 0;
                g = c;
                b = x;
            } else if (h >= 180 && h < 240) {
                r = 0;
                g = x;
                b = c;
            } else if (h >= 240 && h < 300) {
                r = x;
                g = 0;
                b = c;
            } else {
                r = c;
                g = 0;
                b = x;
            }
            result[i] = new Pixel(r + m, g + m, b + m, color.getAlpha());
        }

        return result;
    }

    public static Pixel[] convertRgbToHsv(Pixel[] pixelData, Channel channel) {
        Pixel[] result = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getFirst();
            double g = color.getSecond();
            double b = color.getThird();

            double max = Math.max(Math.max(r, g), b);
            double min = Math.min(Math.min(r, g), b);
            double delta = max - min;

            double h;
            if (delta == 0) {
                h = 0;
            } else if (max == r && g >= b) {
                h = 60 * (g - b) / delta;
            } else if (max == r) {
                h = 60 * (g - b) / delta + 360;
            } else if (max == g) {
                h = 60 * (b - r) / delta + 120;
            } else {
                h = 60 * (r - g) / delta + 240;
            }

            double s = max == 0 ? 0 : 1 - min / max;
            double v = max;

            switch (channel) {
                case ALL -> {
                    h = h;
                    s = s;
                    v = v;
                }
                case FIRST -> {
                    h = h;
                    s = 1;
                    v = 1;
                }
                case SECOND -> {
                    h = 0;
                    s = s;
                    v = 1; // макс
                }
                case THIRD -> {
                    h = 0;
                    s = 0;
                    v = v;
                }
            }


            Pixel pixel = new Pixel(h, s, v, 1);
            result[i] = pixel;
        }

        return result;
    }


    public static Pixel[] convertHsvToRgb(Pixel[] pixelData, Channel channel) {
        Pixel[] result = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double h = color.getFirst() * 360;
            double s = color.getSecond();
            double v = color.getThird();

            double c = v * s;
            double x = c * (1 - Math.abs((h / 60) % 2 - 1));
            double m = v - c;

            double r, g, b;

            if (0 <= h && h < 60) {
                r = c;
                g = x;
                b = 0;
            } else if (60 <= h && h < 120) {
                r = x;
                g = c;
                b = 0;
            } else if (120 <= h && h < 180) {
                r = 0;
                g = c;
                b = x;
            } else if (180 <= h && h < 240) {
                r = 0;
                g = x;
                b = c;
            } else if (240 <= h && h < 300) {
                r = x;
                g = 0;
                b = c;
            } else {
                r = c;
                g = 0;
                b = x;
            }

            result[i] = new Pixel(r + m, g + m, b + m, color.getAlpha());
        }

        return result;
    }


    public static Pixel[] convertYCbCr601ToRgb(Pixel[] pixelData, Channel channel) {
        return convertYCbCrToRgb(pixelData, channel, YCbCrFormat.YCBCR601);
    }

    public static Pixel[] convertYCbCr709ToRgb(Pixel[] pixelData, Channel channel) {
        return convertYCbCrToRgb(pixelData, channel, YCbCrFormat.YCBCR709);
    }

    public static Pixel[] convertRgbToYCbCr601(Pixel[] pixelData, Channel channel) {
        return convertRgbToYCbCr(pixelData, channel, YCbCrFormat.YCBCR601);
    }

    public static Pixel[] convertRgbToYCbCr709(Pixel[] pixelData, Channel channel) {
        return convertRgbToYCbCr(pixelData, channel, YCbCrFormat.YCBCR709);
    }

    private static Pixel[] convertRgbToYCbCr(Pixel[] pixelData, Channel channel, YCbCrFormat format) {
        Pixel[] result = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double y = color.getFirst() * 255;
            double cb = color.getSecond() * 255;
            double cr = color.getThird() * 255;

            double yd = y;
            double cbd = cb - 128;
            double crd = cr - 128;

            int red = 0;
            int green = 0;
            int blue = 0;

            switch (format) {
                case YCBCR601 -> {
                    red = (int) Math.round(yd + 1.402 * crd);
                    green = (int) Math.round(yd - 0.3441 * cbd - 0.7141 * crd);
                    blue = (int) Math.round(yd + 1.772 * cbd);
                }
                case YCBCR709 -> {
                    red = (int) Math.round(yd + 1.5748 * crd);
                    green = (int) Math.round(yd - 0.1873 * cbd - 0.4681 * crd);
                    blue = (int) Math.round(yd + 1.8556 * cbd);
                }
            }


            red = Math.min(255, Math.max(0, red));
            green = Math.min(255, Math.max(0, green));
            blue = Math.min(255, Math.max(0, blue));

            result[i] = new Pixel((double) red / 255, (double) green / 255, (double) blue / 255, color.getAlpha());
        }
        return result;
    }

    private static Pixel[] convertYCbCrToRgb(Pixel[] pixelData, Channel channel, YCbCrFormat format) {
        Pixel[] result = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getFirst() * 255;
            double g = color.getSecond() * 255;
            double b = color.getThird() * 255;

            double y = 0;
            double cb = 0;
            double cr = 0;

            switch (format) {
                case YCBCR601 -> {
                    y = 0.299 * r + 0.587 * g + 0.114 * b;
                    cb = -0.1687 * r - 0.3313 * g + 0.5 * b + 128;
                    cr = 0.5 * r - 0.4187 * g - 0.0813 * b + 128;
                }
                case YCBCR709 -> {
                    y = 0.2126 * r + 0.7152 * g + 0.0722 * b;
                    cb = -0.1146 * r - 0.3854 * g + 0.5 * b + 128;
                    cr = 0.5 * r - 0.4542 * g - 0.0458 * b + 128;
                }
            }
            switch (channel) {
                case ALL -> {
                    y = y;
                    cb = cb;
                    cr = cr;
                }
                case FIRST -> {
                    y = y;
                    cb = 128;
                    cr = 128;
                }
                case SECOND -> {
                    y = 128;
                    cb = cb;
                    cr = 128;
                }
                case THIRD -> {
                    y = 128;
                    cb = 128;
                    cr = cr;
                }
            }


            result[i] = new Pixel(y / 255, cb / 255, cr / 255, color.getAlpha());
        }
        return result;
    }

    public static Pixel[] convertYCoCgToRgb(Pixel[] pixelData, Channel channel) {
        Pixel[] result = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double y = color.getFirst() * 360;
            double co = color.getSecond();
            double cg = color.getThird();

            result[i] = new Pixel(y + co - cg, y + cg, y - co - cg, color.getAlpha());
        }

        return result;
    }

    public static Pixel[] convertRgbToYCoCg(Pixel[] pixelData, Channel channel) {
        Pixel[] result = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getFirst();
            double g = color.getSecond();
            double b = color.getThird();

            double y = r / 4 + g / 2 + b / 4;
            double co = r / 2 - b / 2;
            double cg = -r / 4 + g / 2 - b / 4;

            switch (channel) {
                case ALL -> {
                    y = y;
                    co = co;
                    cg = cg;
                }
                case FIRST -> {
                    y = y;
                    co = 0;
                    cg = 0;
                }
                case SECOND -> {
                    y = 0.5;
                    co = co;
                    cg = 0;
                }
                case THIRD -> {
                    y = 0.5;
                    co = 0;
                    cg = cg;
                }
            }

            Pixel pixel = new Pixel(y, co, cg, color.getAlpha());
            result[i] = pixel;
        }

        return result;
    }

    public static Pixel[] convertCmyToRgb(Pixel[] pixelData, Channel channel) {
        Pixel[] result = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double c = color.getFirst() * 360;
            double m = color.getSecond();
            double y = color.getThird();

            result[i] = new Pixel(1 - c, 1 - m, 1 - y, color.getAlpha());
        }

        return result;
    }

    public static Pixel[] convertRgbToCmy(Pixel[] pixelData, Channel channel) {
        Pixel[] result = new Pixel[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getFirst();
            double g = color.getSecond();
            double b = color.getThird();

            double c = 1.0 - r;
            double m = 1.0 - g;
            double y = 1.0 - b;

            switch (channel) {
                case ALL -> {
                    c = c;
                    m = m;
                    y = y;
                }
                case FIRST -> {
                    c = c;
                    m = 0;
                    y = 0;
                }
                case SECOND -> {
                    c = 0;
                    m = m;
                    y = 0;
                }
                case THIRD -> {
                    c = 0;
                    m = 0;
                    y = y;
                }
            }

            Pixel pixel = new Pixel(c, m, y, color.getAlpha());
            result[i] = pixel;
        }

        return result;
    }

    public static int[] convertToRgb(Pixel[] pixelData, Channel channel) {
        int[] intRgba = new int[pixelData.length];
        int[] pos = POSITIONS_MAP.get(channel);
        for (int i = 0; i < pixelData.length; i++) {
            double[] rgba = pixelData[i].getColors();
            int r = pos[0] * (int) (rgba[0] * 255);
            int g = pos[1] * (int) (rgba[1] * 255);
            int bl = pos[2] * (int) (rgba[2] * 255);
            int alpha = 255;
            intRgba[i] = (alpha << 24) + (r << 16) + (g << 8) + (bl);
        }
        return intRgba;
    }

}
